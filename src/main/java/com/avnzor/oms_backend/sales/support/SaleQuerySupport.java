package com.avnzor.oms_backend.sales.support;

import com.avnzor.oms_backend.sales.entity.Sale;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SaleQuerySupport {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public long countSales(String status, String search) {
        SaleFilterQuery query = buildFilterQuery(status, search, false);
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sma_sales s " + query.whereClause(), Long.class, query.params().toArray());
        return count == null ? 0L : count;
    }

    public List<Sale> findSales(String status, String search, String sortBy, String sortOrder, int page, int limit) {
        SaleFilterQuery query = buildFilterQuery(status, search, true);
        String orderClause = resolveOrderClause(sortBy, sortOrder);
        int offset = Math.max(page - 1, 0) * limit;

        String sql = """
                SELECT s.*
                FROM sma_sales s
                %s
                %s
                LIMIT ? OFFSET ?
                """.formatted(query.whereClause(), orderClause);

        List<Object> params = new ArrayList<>(query.params());
        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Sale sale = new Sale();
            sale.setId(rs.getInt("id"));
            sale.setReferenceNo(rs.getString("reference_no"));
            sale.setCustomer(rs.getString("customer"));
            sale.setCustomerEmail(rs.getString("customer_email"));
            sale.setShippingFirstName(rs.getString("shipping_first_name"));
            sale.setShippingLastName(rs.getString("shipping_last_name"));
            sale.setShippingPhone(rs.getString("shipping_phone"));
            sale.setShippingName(rs.getString("shipping_name"));
            if (rs.getTimestamp("date") != null) {
                sale.setDate(rs.getTimestamp("date").toLocalDateTime());
            }
            sale.setJobType(rs.getString("job_type"));
            sale.setSaleStatus(rs.getString("sale_status"));
            sale.setPaymentStatus(rs.getString("payment_status"));
            sale.setCourierOrderTrackingId(rs.getString("courier_order_tracking_id"));
            sale.setSourceName(rs.getString("source_name"));
            sale.setShopifyTags(rs.getString("shopify_tags"));
            sale.setTotalItems(rs.getObject("total_items") == null ? null : rs.getInt("total_items"));
            return sale;
        }, params.toArray());
    }

    public Sale findByReferenceNo(String referenceNo) {
        String cleaned = referenceNo.replaceFirst("^#", "").trim();
        String sql = """
                SELECT s.*
                FROM sma_sales s
                WHERE TRIM(LEADING '#' FROM s.reference_no) = ?
                LIMIT 1
                """;
        List<Sale> sales = jdbcTemplate.query(sql, (rs, rowNum) -> mapSale(rs), cleaned);
        return sales.isEmpty() ? null : sales.getFirst();
    }

    private Sale mapSale(java.sql.ResultSet rs) throws java.sql.SQLException {
        Sale sale = new Sale();
        sale.setId(rs.getInt("id"));
        sale.setReferenceNo(rs.getString("reference_no"));
        sale.setCustomer(rs.getString("customer"));
        sale.setCustomerEmail(rs.getString("customer_email"));
        sale.setCustomerId(rs.getObject("customer_id") == null ? null : rs.getInt("customer_id"));
        sale.setAddressId(rs.getObject("address_id") == null ? null : rs.getInt("address_id"));
        sale.setShippingFirstName(rs.getString("shipping_first_name"));
        sale.setShippingLastName(rs.getString("shipping_last_name"));
        sale.setShippingPhone(rs.getString("shipping_phone"));
        sale.setShippingName(rs.getString("shipping_name"));
        sale.setShippingAddress1(rs.getString("shipping_address1"));
        sale.setShippingAddress2(rs.getString("shipping_address2"));
        sale.setShippingCity(rs.getString("shipping_city"));
        sale.setShippingZip(rs.getString("shipping_zip"));
        sale.setShippingCountry(rs.getString("shipping_country"));
        sale.setShippingLatitude(rs.getString("shipping_latitude"));
        sale.setShippingLongitude(rs.getString("shipping_longitude"));
        if (rs.getTimestamp("date") != null) {
            sale.setDate(rs.getTimestamp("date").toLocalDateTime());
        }
        sale.setJobType(rs.getString("job_type"));
        sale.setSaleStatus(rs.getString("sale_status"));
        sale.setPaymentStatus(rs.getString("payment_status"));
        sale.setCourierLabel(rs.getString("courier_label"));
        sale.setCourierOrderStatus(rs.getString("courier_order_status"));
        sale.setCourierOrderTrackingId(rs.getString("courier_order_tracking_id"));
        sale.setSourceName(rs.getString("source_name"));
        sale.setShopifyTags(rs.getString("shopify_tags"));
        sale.setTotalItems(rs.getObject("total_items") == null ? null : rs.getInt("total_items"));
        sale.setGrandTotal(rs.getBigDecimal("grand_total"));
        sale.setTotalDiscount(rs.getBigDecimal("total_discount"));
        sale.setShipping(rs.getBigDecimal("shipping"));
        sale.setTotalTax(rs.getBigDecimal("total_tax"));
        sale.setProductTax(rs.getBigDecimal("product_tax"));
        sale.setOrderTax(rs.getBigDecimal("order_tax"));
        sale.setNote(rs.getString("note"));
        sale.setStaffNote(rs.getString("staff_note"));
        sale.setWarehouseId(rs.getObject("warehouse_id") == null ? null : rs.getInt("warehouse_id"));
        sale.setPaymentMethod(rs.getString("payment_method"));
        if (rs.getTimestamp("updated_at") != null) {
            sale.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return sale;
    }

    public Map<Integer, Map<String, Object>> loadListEnrichment(List<Integer> saleIds) {
        if (saleIds.isEmpty()) {
            return Map.of();
        }
        MapSqlParameterSource params = new MapSqlParameterSource("saleIds", saleIds);
        String sql = """
                SELECT s.id AS sale_id,
                       (SELECT COUNT(*) FROM sma_sale_items si WHERE si.sale_id = s.id) AS items_count
                FROM sma_sales s
                WHERE s.id IN (:saleIds)
                """;
        Map<Integer, Map<String, Object>> result = new java.util.HashMap<>();
        namedJdbcTemplate.query(sql, params, rs -> {
            result.put(rs.getInt("sale_id"), Map.of("items_count", rs.getInt("items_count")));
        });
        return result;
    }

    private SaleFilterQuery buildFilterQuery(String status, String search, boolean prefixWhere) {
        List<Object> params = new ArrayList<>();
        List<String> clauses = new ArrayList<>();

        if (status != null && !status.isBlank()) {
            List<String> statuses = List.of(status.split(",")).stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            if (!statuses.isEmpty()) {
                String placeholders = String.join(",", statuses.stream().map(s -> "?").toList());
                clauses.add("s.sale_status IN (" + placeholders + ")");
                params.addAll(statuses);
            }
        }

        if (search != null && !search.isBlank()) {
            String term = "%" + search.trim() + "%";
            String normalizedCode = ProductCodeNormalizer.normalize(search.trim());
            StringBuilder searchClause = new StringBuilder("""
                    (
                      s.customer LIKE ?
                      OR s.customer_email LIKE ?
                      OR s.shipping_name LIKE ?
                      OR s.shipping_phone LIKE ?
                      OR s.reference_no LIKE ?
                      OR EXISTS (
                        SELECT 1 FROM sma_sale_items si
                        WHERE si.sale_id = s.id
                          AND TRIM(LEADING '0' FROM si.product_code) = ?
                      )
                    """);
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(normalizedCode);
            if (search.trim().matches("\\d+")) {
                searchClause.append(" OR s.id = ?");
                params.add(Integer.parseInt(search.trim()));
            }
            searchClause.append(")");
            clauses.add(searchClause.toString());
        }

        if (clauses.isEmpty()) {
            return new SaleFilterQuery("", params);
        }

        String where = (prefixWhere ? "WHERE " : "AND ") + String.join(" AND ", clauses);
        return new SaleFilterQuery(where, params);
    }

    private String resolveOrderClause(String sortBy, String sortOrder) {
        String direction = "DESC".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
        String column = switch (sortBy == null ? "" : sortBy) {
            case "quantity", "total_items" -> "s.total_items";
            case "reference_no" -> "s.reference_no";
            case "customer" -> "s.customer";
            case "id" -> "s.id";
            default -> "s.date";
        };
        return "ORDER BY " + column + " " + direction;
    }

    private record SaleFilterQuery(String whereClause, List<Object> params) {
    }
}
