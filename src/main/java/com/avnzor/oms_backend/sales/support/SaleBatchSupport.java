package com.avnzor.oms_backend.sales.support;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SaleBatchSupport {

    private final JdbcTemplate jdbcTemplate;

    public List<SaleBatchView> loadBatchesForProduct(String productCode) {
        return loadBatchesForProducts(List.of(productCode))
                .getOrDefault(ProductCodeNormalizer.normalize(productCode), List.of());
    }

    public Map<String, List<SaleBatchView>> loadBatchesForProducts(Collection<String> productCodes) {
        Set<String> normalizedCodes = new LinkedHashSet<>();
        for (String productCode : productCodes) {
            if (productCode == null || productCode.isBlank()) {
                continue;
            }
            normalizedCodes.add(ProductCodeNormalizer.normalize(productCode));
        }
        if (normalizedCodes.isEmpty()) {
            return Map.of();
        }

        String placeholders = String.join(",", normalizedCodes.stream().map(code -> "?").toList());
        String sql = """
                SELECT TRIM(LEADING '0' FROM si.product_code) AS product_code,
                       si.batch_no,
                       si.expiry_date,
                       COALESCE(SUM(si.qty), 0) AS total_qty,
                       s.box_number,
                       s.id AS shelving_id
                FROM sma_purchase_order_shelving_items si
                INNER JOIN sma_purchase_order_shelving s ON s.id = si.shelving_id
                WHERE TRIM(LEADING '0' FROM si.product_code) IN (%s)
                  AND si.qty > 0
                GROUP BY product_code, si.batch_no, si.expiry_date, s.box_number, s.id
                """.formatted(placeholders);

        List<Map<String, Object>> rows;
        try {
            rows = jdbcTemplate.queryForList(sql, normalizedCodes.toArray());
        } catch (Exception ex) {
            return Map.of();
        }

        Map<String, Map<String, SaleBatchView>> groupedByProduct = new LinkedHashMap<>();
        Set<String> allBoxes = new LinkedHashSet<>();
        for (Map<String, Object> row : rows) {
            String productCode = ProductCodeNormalizer.normalize(String.valueOf(row.get("product_code")));
            String batchKey = row.get("batch_no") == null ? "NO_BATCH" : String.valueOf(row.get("batch_no"));
            Map<String, SaleBatchView> grouped = groupedByProduct.computeIfAbsent(productCode, key -> new LinkedHashMap<>());

            SaleBatchView existing = grouped.get(batchKey);
            BigDecimal qty = toBigDecimal(row.get("total_qty"));
            String boxQty = buildBoxQty(row);
            collectBoxes(boxQty, allBoxes);

            if (existing == null) {
                grouped.put(batchKey, new SaleBatchView(
                        batchKey.equals("NO_BATCH") ? null : batchKey,
                        row.get("expiry_date") == null ? null : String.valueOf(row.get("expiry_date")),
                        qty,
                        boxQty,
                        List.of()
                ));
            } else {
                String mergedBoxQty = mergeBoxQty(existing.shelvingIdBoxQty(), boxQty);
                collectBoxes(mergedBoxQty, allBoxes);
                grouped.put(batchKey, new SaleBatchView(
                        existing.batchNo(),
                        existing.expiryDate(),
                        existing.totalQty().add(qty),
                        mergedBoxQty,
                        List.of()
                ));
            }
        }

        Map<String, String> statusByBox = loadLocationStatusesByBoxes(allBoxes);
        Map<String, List<SaleBatchView>> result = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, SaleBatchView>> entry : groupedByProduct.entrySet()) {
            List<SaleBatchView> batches = new ArrayList<>();
            for (SaleBatchView batch : entry.getValue().values()) {
                batches.add(new SaleBatchView(
                        batch.batchNo(),
                        batch.expiryDate(),
                        batch.totalQty(),
                        batch.shelvingIdBoxQty(),
                        resolveLocationStatuses(batch.shelvingIdBoxQty(), statusByBox)
                ));
            }
            result.put(entry.getKey(), batches);
        }
        return result;
    }

    private void collectBoxes(String shelvingIdBoxQty, Set<String> boxes) {
        if (shelvingIdBoxQty == null || shelvingIdBoxQty.isBlank()) {
            return;
        }
        for (String part : shelvingIdBoxQty.split(",")) {
            String[] segments = part.trim().split(":");
            if (segments.length >= 2 && !segments[1].isBlank()) {
                boxes.add(segments[1].trim());
            }
        }
    }

    private Map<String, String> loadLocationStatusesByBoxes(Set<String> boxes) {
        if (boxes.isEmpty()) {
            return Map.of();
        }

        String placeholders = String.join(",", boxes.stream().map(box -> "?").toList());
        String sql = "SELECT box, status FROM sma_warehouse_locations WHERE box IN (" + placeholders + ")";
        Map<String, String> statusByBox = new HashMap<>();
        try {
            jdbcTemplate.queryForList(sql, boxes.toArray()).forEach(row ->
                    statusByBox.put(
                            String.valueOf(row.get("box")),
                            row.get("status") == null ? null : String.valueOf(row.get("status"))
                    )
            );
        } catch (Exception ignored) {
            return Map.of();
        }
        return statusByBox;
    }

    public String extractSortBoxKey(List<SaleBatchView> batches) {
        for (SaleBatchView batch : batches) {
            if (batch.shelvingIdBoxQty() == null || batch.shelvingIdBoxQty().isBlank()) {
                continue;
            }
            String first = batch.shelvingIdBoxQty().split(",")[0].trim();
            String box = first.split(":")[0].trim();
            if (!box.isBlank()) {
                return box.toUpperCase();
            }
        }
        return "ZZZ";
    }

    private String buildBoxQty(Map<String, Object> row) {
        Object shelvingId = row.get("shelving_id");
        Object boxNumber = row.get("box_number");
        Object qty = row.get("total_qty");
        if (shelvingId == null || boxNumber == null) {
            return null;
        }
        return shelvingId + ":" + boxNumber + ":" + qty;
    }

    private String mergeBoxQty(String existing, String additional) {
        if (existing == null || existing.isBlank()) {
            return additional;
        }
        if (additional == null || additional.isBlank()) {
            return existing;
        }
        return existing + ", " + additional;
    }

    private List<LocationStatusView> resolveLocationStatuses(String shelvingIdBoxQty, Map<String, String> statusByBox) {
        if (shelvingIdBoxQty == null || shelvingIdBoxQty.isBlank()) {
            return List.of();
        }

        Set<String> boxes = new LinkedHashSet<>();
        for (String part : shelvingIdBoxQty.split(",")) {
            String[] segments = part.trim().split(":");
            if (segments.length >= 2 && !segments[1].isBlank()) {
                boxes.add(segments[1].trim());
            }
        }
        if (boxes.isEmpty()) {
            return List.of();
        }

        List<LocationStatusView> statuses = new ArrayList<>();
        for (String box : boxes) {
            statuses.add(new LocationStatusView(box, statusByBox.get(box)));
        }
        return statuses;
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

    public record SaleBatchView(
            String batchNo,
            String expiryDate,
            BigDecimal totalQty,
            String shelvingIdBoxQty,
            List<LocationStatusView> locationStatus
    ) {
    }

    public record LocationStatusView(String box, String status) {
    }
}
