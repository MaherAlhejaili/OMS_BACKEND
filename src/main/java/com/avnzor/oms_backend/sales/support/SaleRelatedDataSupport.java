package com.avnzor.oms_backend.sales.support;

import com.avnzor.oms_backend.customers.entity.Address;
import com.avnzor.oms_backend.customers.entity.Company;
import com.avnzor.oms_backend.customers.repository.AddressRepository;
import com.avnzor.oms_backend.customers.repository.CompanyRepository;
import com.avnzor.oms_backend.employees.entity.Employee;
import com.avnzor.oms_backend.employees.repository.EmployeeRepository;
import com.avnzor.oms_backend.products.entity.Product;
import com.avnzor.oms_backend.products.repository.ProductRepository;
import com.avnzor.oms_backend.sales.dto.SaleSummaryResponse;
import com.avnzor.oms_backend.sales.entity.Sale;
import com.avnzor.oms_backend.sales.entity.SalesJob;
import com.avnzor.oms_backend.sales.entity.ShopifyOrder;
import com.avnzor.oms_backend.sales.repository.SalesJobRepository;
import com.avnzor.oms_backend.sales.repository.ShopifyOrderRepository;
import com.avnzor.oms_backend.shipping.entity.ShipmentCreationLog;
import com.avnzor.oms_backend.shipping.repository.ShipmentCreationLogRepository;
import com.avnzor.oms_backend.suppliers.entity.SupplierInventory;
import com.avnzor.oms_backend.suppliers.entity.SupplierOrder;
import com.avnzor.oms_backend.suppliers.repository.SupplierInventoryRepository;
import com.avnzor.oms_backend.suppliers.repository.SupplierOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SaleRelatedDataSupport {

    private final CompanyRepository companyRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final ShopifyOrderRepository shopifyOrderRepository;
    private final SalesJobRepository salesJobRepository;
    private final SupplierInventoryRepository supplierInventoryRepository;
    private final SupplierOrderRepository supplierOrderRepository;
    private final EmployeeRepository employeeRepository;
    private final ShipmentCreationLogRepository shipmentCreationLogRepository;
    private final JdbcTemplate jdbcTemplate;

    public Optional<Company> findCustomer(Sale sale) {
        if (sale.getCustomerId() == null) {
            return Optional.empty();
        }
        return companyRepository.findById(sale.getCustomerId());
    }

    public Optional<Address> findAddress(Sale sale) {
        if (sale.getAddressId() == null) {
            return Optional.empty();
        }
        return addressRepository.findById(sale.getAddressId());
    }

    public String resolveShopifySaleId(Sale sale) {
        if (sale.getReferenceNo() == null || sale.getReferenceNo().isBlank()) {
            return null;
        }
        String reference = sale.getReferenceNo().trim();
        String withoutHash = reference.replaceFirst("^#", "").trim();

        return shopifyOrderRepository.findFirstByOrderNumber(reference)
                .or(() -> shopifyOrderRepository.findFirstByOrderNumber(withoutHash))
                .map(ShopifyOrder::getShopifyOrderId)
                .map(String::valueOf)
                .orElse(null);
    }

    public Map<String, String> loadProductImages(List<String> productCodes) {
        if (productCodes.isEmpty()) {
            return Map.of();
        }
        Map<String, String> images = new HashMap<>();
        for (Product product : productRepository.findByCodeIn(productCodes)) {
            if (product.getCode() != null && product.getImage() != null) {
                images.put(product.getCode().trim(), product.getImage());
                images.put(ProductCodeNormalizer.normalize(product.getCode()), product.getImage());
            }
        }
        return images;
    }

    public Map<Integer, String> loadShopifySaleIds(List<Sale> sales) {
        Map<Integer, String> result = new HashMap<>();
        for (Sale sale : sales) {
            String shopifySaleId = resolveShopifySaleId(sale);
            if (shopifySaleId != null) {
                result.put(sale.getId(), shopifySaleId);
            }
        }
        return result;
    }

    public Map<Integer, SalesJob> loadJobsBySaleIds(Collection<Integer> saleIds) {
        if (saleIds.isEmpty()) {
            return Map.of();
        }
        return salesJobRepository.findByReferenceNoIn(
                        saleIds.stream().map(String::valueOf).toList()
                ).stream()
                .collect(Collectors.toMap(
                        job -> Integer.valueOf(job.getReferenceNo()),
                        job -> job,
                        (left, right) -> left
                ));
    }

    public Optional<SalesJob> findJobForSale(Integer saleId) {
        return salesJobRepository.findFirstByReferenceNo(String.valueOf(saleId));
    }

    public SaleSummaryResponse.SaleJobResponse toJobResponse(SalesJob job) {
        if (job == null) {
            return null;
        }
        String employeeName = resolveEmployeeName(job.getAssignedTo());
        return new SaleSummaryResponse.SaleJobResponse(
                job.getId(),
                job.getAssignedTo(),
                job.getAssignedBy(),
                job.getCreatedAt(),
                employeeName
        );
    }

    public Map<Integer, SupplierOrder> loadSupplierOrdersBySaleIds(Collection<Integer> saleIds) {
        if (saleIds.isEmpty()) {
            return Map.of();
        }
        return supplierOrderRepository.findByOrderIdIn(saleIds).stream()
                .collect(Collectors.toMap(SupplierOrder::getOrderId, order -> order, (left, right) -> left));
    }

    public Optional<SupplierOrder> findSupplierOrderForSale(Integer saleId) {
        return supplierOrderRepository.findFirstByOrderId(saleId);
    }

    private String resolveEmployeeName(Integer employeeId) {
        if (employeeId == null) {
            return null;
        }
        return employeeRepository.findById(employeeId)
                .map(Employee::getName)
                .orElse(null);
    }

    public Map<String, Integer> loadSupplierIdsByProductCodes(List<String> productCodes) {
        if (productCodes.isEmpty()) {
            return Map.of();
        }

        Set<String> lookupCodes = new HashSet<>();
        for (String code : productCodes) {
            if (code == null || code.isBlank()) {
                continue;
            }
            lookupCodes.add(code.trim());
            lookupCodes.add(ProductCodeNormalizer.normalize(code));
        }
        if (lookupCodes.isEmpty()) {
            return Map.of();
        }

        Map<String, Integer> supplierByCode = new HashMap<>();
        for (SupplierInventory inventory : supplierInventoryRepository.findByProductCodeIn(lookupCodes)) {
            if (inventory.getProductCode() == null || inventory.getSupplierId() == null) {
                continue;
            }
            String trimmed = inventory.getProductCode().trim();
            supplierByCode.put(trimmed, inventory.getSupplierId());
            supplierByCode.put(ProductCodeNormalizer.normalize(trimmed), inventory.getSupplierId());
        }
        return supplierByCode;
    }

    public List<ShipmentCreationLog> loadShipments(Integer saleId) {
        try {
            return shipmentCreationLogRepository.findByOrderIdOrderByCreatedAtAsc(saleId);
        } catch (Exception ex) {
            return List.of();
        }
    }

    public List<ShipmentItemRow> loadShipmentItems(Integer saleId) {
        try {
            return jdbcTemplate.query(
                    """
                            SELECT id, item_id, product_code, qty, tracking_id, carrier
                            FROM sma_shipment_items
                            WHERE order_id = ?
                            """,
                    (rs, rowNum) -> new ShipmentItemRow(
                            rs.getInt("id"),
                            rs.getInt("item_id"),
                            rs.getString("product_code"),
                            rs.getBigDecimal("qty"),
                            rs.getString("tracking_id"),
                            rs.getString("carrier")
                    ),
                    saleId
            );
        } catch (Exception ex) {
            return List.of();
        }
    }

    public Map<Integer, List<SaleSummaryResponse.SaleShipmentSummaryResponse>> loadShipmentSummariesBySaleIds(
            Collection<Integer> saleIds
    ) {
        if (saleIds.isEmpty()) {
            return Map.of();
        }

        String placeholders = saleIds.stream().map(id -> "?").collect(Collectors.joining(","));
        Map<Integer, List<SaleSummaryResponse.SaleShipmentSummaryResponse>> result = new HashMap<>();

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT order_id, tracking_id, carrier FROM sma_shipment_items WHERE order_id IN (" + placeholders + ")",
                    saleIds.toArray()
            );

            Map<String, String> statusByTracking = loadTrackingStatusesFromRows(rows);

            for (Map<String, Object> row : rows) {
                Integer saleId = ((Number) row.get("order_id")).intValue();
                String trackingId = row.get("tracking_id") == null ? null : String.valueOf(row.get("tracking_id"));
                if (trackingId == null || trackingId.isBlank()) {
                    continue;
                }

                List<SaleSummaryResponse.SaleShipmentSummaryResponse> shipments =
                        result.computeIfAbsent(saleId, key -> new ArrayList<>());

                boolean exists = shipments.stream().anyMatch(shipment -> trackingId.equals(shipment.trackingId()));
                if (exists) {
                    continue;
                }

                String carrier = row.get("carrier") == null ? null : String.valueOf(row.get("carrier"));
                String rawStatus = statusByTracking.getOrDefault(trackingId, "created");
                shipments.add(new SaleSummaryResponse.SaleShipmentSummaryResponse(trackingId, carrier, rawStatus));
            }
        } catch (Exception ignored) {
            return Map.of();
        }

        return result;
    }

    public String resolveShippingPhone(Sale sale, Optional<Company> customer) {
        if (sale.getShippingPhone() != null && !sale.getShippingPhone().isBlank()) {
            return sale.getShippingPhone();
        }

        Optional<Address> address = findAddress(sale);
        if (address.map(Address::getPhone).filter(phone -> !phone.isBlank()).isPresent()) {
            return address.get().getPhone();
        }

        return customer.map(Company::getPhone).orElse(null);
    }

    public String resolveCustomerName(Sale sale, Optional<Company> customer) {
        if (sale.getShippingName() != null && !sale.getShippingName().isBlank()) {
            return sale.getShippingName();
        }
        return customer.map(company -> {
            if (company.getCompany() != null && !company.getCompany().isBlank()) {
                return company.getCompany();
            }
            if (company.getName() != null && !company.getName().isBlank()) {
                return company.getName();
            }
            return null;
        }).orElse(sale.getCustomer() == null ? "N/A" : sale.getCustomer());
    }

    public String resolveCustomerEmail(Sale sale, Optional<Company> customer) {
        if (sale.getCustomerEmail() != null && !sale.getCustomerEmail().isBlank()) {
            return sale.getCustomerEmail();
        }
        return customer.map(Company::getEmail).orElse(null);
    }

    private Map<String, String> loadTrackingStatusesFromRows(List<Map<String, Object>> rows) {
        Set<String> trackingIds = rows.stream()
                .map(row -> row.get("tracking_id"))
                .filter(value -> value != null)
                .map(String::valueOf)
                .filter(id -> !id.isBlank())
                .collect(Collectors.toSet());
        if (trackingIds.isEmpty()) {
            return Map.of();
        }

        String placeholders = trackingIds.stream().map(id -> "?").collect(Collectors.joining(","));
        Map<String, String> statuses = new HashMap<>();
        try {
            jdbcTemplate.query(
                    "SELECT tracking_number, status FROM sma_shipment_creations WHERE tracking_number IN (" + placeholders + ")",
                    (rs, rowNum) -> statuses.put(rs.getString("tracking_number"), rs.getString("status")),
                    trackingIds.toArray()
            );
        } catch (Exception ignored) {
            return Map.of();
        }
        return statuses;
    }

    public record ShipmentItemRow(
            Integer id,
            Integer itemId,
            String productCode,
            java.math.BigDecimal qty,
            String trackingId,
            String carrier
    ) {
    }
}
