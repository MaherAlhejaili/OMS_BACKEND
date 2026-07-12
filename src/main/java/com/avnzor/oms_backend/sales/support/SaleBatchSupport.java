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
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SaleBatchSupport {

    private final JdbcTemplate jdbcTemplate;

    public List<SaleBatchView> loadBatchesForProduct(String productCode) {
        String normalized = ProductCodeNormalizer.normalize(productCode);
        String sql = """
                SELECT si.batch_no,
                       si.expiry_date,
                       COALESCE(SUM(si.qty), 0) AS total_qty,
                       s.box_number,
                       s.id AS shelving_id
                FROM sma_purchase_order_shelving_items si
                INNER JOIN sma_purchase_order_shelving s ON s.id = si.shelving_id
                WHERE TRIM(LEADING '0' FROM si.product_code) = ?
                  AND si.qty > 0
                GROUP BY si.batch_no, si.expiry_date, s.box_number, s.id
                """;

        List<Map<String, Object>> rows;
        try {
            rows = jdbcTemplate.queryForList(sql, normalized);
        } catch (Exception ex) {
            return List.of();
        }

        Map<String, SaleBatchView> grouped = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String batchKey = row.get("batch_no") == null ? "NO_BATCH" : String.valueOf(row.get("batch_no"));
            SaleBatchView existing = grouped.get(batchKey);
            BigDecimal qty = toBigDecimal(row.get("total_qty"));
            String boxQty = buildBoxQty(row);

            if (existing == null) {
                grouped.put(batchKey, new SaleBatchView(
                        batchKey.equals("NO_BATCH") ? null : batchKey,
                        row.get("expiry_date") == null ? null : String.valueOf(row.get("expiry_date")),
                        qty,
                        boxQty,
                        resolveLocationStatuses(boxQty)
                ));
            } else {
                grouped.put(batchKey, new SaleBatchView(
                        existing.batchNo(),
                        existing.expiryDate(),
                        existing.totalQty().add(qty),
                        mergeBoxQty(existing.shelvingIdBoxQty(), boxQty),
                        resolveLocationStatuses(mergeBoxQty(existing.shelvingIdBoxQty(), boxQty))
                ));
            }
        }
        return new ArrayList<>(grouped.values());
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

    private List<LocationStatusView> resolveLocationStatuses(String shelvingIdBoxQty) {
        if (shelvingIdBoxQty == null || shelvingIdBoxQty.isBlank()) {
            return List.of();
        }

        Set<String> boxes = new java.util.LinkedHashSet<>();
        for (String part : shelvingIdBoxQty.split(",")) {
            String[] segments = part.trim().split(":");
            if (segments.length >= 2 && !segments[1].isBlank()) {
                boxes.add(segments[1].trim());
            }
        }
        if (boxes.isEmpty()) {
            return List.of();
        }

        String placeholders = String.join(",", boxes.stream().map(b -> "?").toList());
        String sql = "SELECT box, status FROM sma_warehouse_locations WHERE box IN (" + placeholders + ")";
        Map<String, String> statusByBox = new HashMap<>();
        try {
            jdbcTemplate.queryForList(sql, boxes.toArray()).forEach(row ->
                    statusByBox.put(String.valueOf(row.get("box")), row.get("status") == null ? null : String.valueOf(row.get("status")))
            );
        } catch (Exception ignored) {
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
