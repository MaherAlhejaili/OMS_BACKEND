package com.avnzor.oms_backend.sales.support;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SaleStockSupport {

    private final JdbcTemplate jdbcTemplate;

    public Map<String, BigDecimal> getShelvingQtyMap(Collection<String> productCodes) {
        Set<String> normalized = new HashSet<>();
        for (String code : productCodes) {
            normalized.add(ProductCodeNormalizer.normalize(code));
        }
        if (normalized.isEmpty()) {
            return Map.of();
        }

        String placeholders = String.join(",", normalized.stream().map(c -> "?").toList());
        String sql = """
                SELECT TRIM(LEADING '0' FROM sh.product_code) AS product_code,
                       COALESCE(SUM(sh.qty), 0) AS shelving_qty
                FROM sma_purchase_order_shelving_items sh
                WHERE TRIM(LEADING '0' FROM sh.product_code) IN (%s)
                GROUP BY TRIM(LEADING '0' FROM sh.product_code)
                """.formatted(placeholders);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, normalized.toArray());
        Map<String, BigDecimal> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            String code = ProductCodeNormalizer.normalize(String.valueOf(row.get("product_code")));
            result.put(code, toBigDecimal(row.get("shelving_qty")));
        }
        return result;
    }

    public ItemSourceClassification classifyItemSources(Collection<String> productCodes) {
        Set<String> normalized = new HashSet<>();
        for (String code : productCodes) {
            normalized.add(ProductCodeNormalizer.normalize(code));
        }
        if (normalized.isEmpty()) {
            return new ItemSourceClassification(Set.of(), Set.of());
        }

        String placeholders = String.join(",", normalized.stream().map(c -> "?").toList());

        String shelvedSql = """
                SELECT DISTINCT TRIM(LEADING '0' FROM posi.product_code) AS product_code
                FROM sma_purchase_order_shelving_items posi
                WHERE TRIM(LEADING '0' FROM posi.product_code) IN (%s)
                """.formatted(placeholders);

        Set<String> shelvedCodes = new HashSet<>();
        jdbcTemplate.queryForList(shelvedSql, normalized.toArray()).forEach(row ->
                shelvedCodes.add(ProductCodeNormalizer.normalize(String.valueOf(row.get("product_code"))))
        );

        Set<String> nonShelved = new HashSet<>(normalized);
        nonShelved.removeAll(shelvedCodes);

        Set<String> externalCodes = new HashSet<>();
        if (!nonShelved.isEmpty()) {
            try {
                String externalPlaceholders = String.join(",", nonShelved.stream().map(c -> "?").toList());
                String externalSql = """
                        SELECT DISTINCT TRIM(LEADING '0' FROM si.product_code) AS product_code
                        FROM sma_supplier_inventory si
                        WHERE TRIM(LEADING '0' FROM si.product_code) IN (%s)
                        """.formatted(externalPlaceholders);
                jdbcTemplate.queryForList(externalSql, nonShelved.toArray()).forEach(row ->
                        externalCodes.add(ProductCodeNormalizer.normalize(String.valueOf(row.get("product_code"))))
                );
            } catch (Exception ignored) {
                // supplier inventory table may not exist in all tenant DBs
            }
        }

        return new ItemSourceClassification(shelvedCodes, externalCodes);
    }

    public Map<String, BigDecimal> getPickedQtyMap(Integer saleId) {
        String sql = """
                SELECT TRIM(LEADING '0' FROM posi.product_code) AS product_code,
                       SUM(ABS(posi.qty)) AS picked_qty
                FROM sma_purchase_order_shelving pos
                INNER JOIN sma_purchase_order_shelving_items posi ON pos.id = posi.shelving_id
                WHERE pos.sale_id = ?
                  AND posi.status = 'picked'
                  AND posi.qty < 0
                GROUP BY TRIM(LEADING '0' FROM posi.product_code)
                """;
        Map<String, BigDecimal> result = new HashMap<>();
        jdbcTemplate.queryForList(sql, saleId).forEach(row -> {
            String code = ProductCodeNormalizer.normalize(String.valueOf(row.get("product_code")));
            result.put(code, toBigDecimal(row.get("picked_qty")));
        });
        return result;
    }

    public String resolveItemStockSource(
            BigDecimal orderedQty,
            BigDecimal shelvingQty,
            boolean isShelved,
            boolean hasExternalCoverage
    ) {
        if (isShelved) {
            return shelvingQty.compareTo(BigDecimal.ZERO) > 0 ? "internal" : "out_of_stock";
        }
        return hasExternalCoverage ? "external" : "out_of_stock";
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        return new BigDecimal(String.valueOf(value));
    }

    public record ItemSourceClassification(Set<String> shelvedCodes, Set<String> externalCodes) {
    }
}
