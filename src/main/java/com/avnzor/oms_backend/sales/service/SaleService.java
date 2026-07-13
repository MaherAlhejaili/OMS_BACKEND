package com.avnzor.oms_backend.sales.service;

import com.avnzor.oms_backend.common.exception.BadRequestException;
import com.avnzor.oms_backend.common.exception.ResourceNotFoundException;
import com.avnzor.oms_backend.customers.entity.Address;
import com.avnzor.oms_backend.customers.entity.Company;
import com.avnzor.oms_backend.sales.dto.EditSaleRequest;
import com.avnzor.oms_backend.sales.dto.PagedSaleListResponse;
import com.avnzor.oms_backend.sales.dto.SaleDetailResponse;
import com.avnzor.oms_backend.sales.dto.SaleSummaryResponse;
import com.avnzor.oms_backend.sales.dto.UpdateSaleStatusRequest;
import com.avnzor.oms_backend.sales.dto.UpdateSaleStatusResponse;
import com.avnzor.oms_backend.sales.dto.UpdateShelvingStatusResponse;
import com.avnzor.oms_backend.sales.entity.Sale;
import com.avnzor.oms_backend.sales.entity.SaleItem;
import com.avnzor.oms_backend.sales.entity.SalesJob;
import com.avnzor.oms_backend.sales.entity.SaleStatusHistory;
import com.avnzor.oms_backend.suppliers.entity.SupplierOrder;
import com.avnzor.oms_backend.sales.repository.SaleItemRepository;
import com.avnzor.oms_backend.sales.repository.SaleRepository;
import com.avnzor.oms_backend.sales.repository.SaleStatusHistoryRepository;
import com.avnzor.oms_backend.sales.support.ProductCodeNormalizer;
import com.avnzor.oms_backend.sales.support.SaleBatchSupport;
import com.avnzor.oms_backend.sales.support.SaleQuerySupport;
import com.avnzor.oms_backend.sales.support.SaleRelatedDataSupport;
import com.avnzor.oms_backend.sales.support.SaleStockSupport;
import com.avnzor.oms_backend.sales.validation.SaleStatus;
import com.avnzor.oms_backend.sales.mapper.SaleMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.avnzor.oms_backend.shipping.entity.ShipmentCreationLog;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private static final Logger log = LoggerFactory.getLogger(SaleService.class);

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final SaleStatusHistoryRepository saleStatusHistoryRepository;
    private final SaleQuerySupport saleQuerySupport;
    private final SaleStockSupport saleStockSupport;
    private final SaleBatchSupport saleBatchSupport;
    private final SaleRelatedDataSupport saleRelatedDataSupport;
    private final SaleMapper saleMapper;
    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true, transactionManager = "tenantTransactionManager")
    public PagedSaleListResponse getAllSales(
            int page,
            int limit,
            String sortBy,
            String sortOrder,
            String status,
            String search
    ) {
        int safePage = Math.max(page, 1);
        int safeLimit = Math.min(Math.max(limit, 1), 100);

        long total = saleQuerySupport.countSales(status, search);
        List<Sale> sales = saleQuerySupport.findSales(status, search, sortBy, sortOrder, safePage, safeLimit);

        List<Integer> saleIds = sales.stream().map(Sale::getId).toList();
        Map<Integer, Map<String, Object>> enrichment = saleQuerySupport.loadListEnrichment(saleIds);
        Map<Integer, String> shopifySaleIds = saleRelatedDataSupport.loadShopifySaleIds(sales);
        Map<Integer, SalesJob> jobsBySaleId = saleRelatedDataSupport.loadJobsBySaleIds(saleIds);
        Map<Integer, SupplierOrder> supplierOrdersBySaleId = saleRelatedDataSupport.loadSupplierOrdersBySaleIds(saleIds);
        Map<Integer, List<SaleSummaryResponse.SaleShipmentSummaryResponse>> shipmentsBySaleId =
                saleRelatedDataSupport.loadShipmentSummariesBySaleIds(saleIds);

        Set<Integer> customerIds = new HashSet<>();
        Set<Integer> addressIds = new HashSet<>();
        for (Sale sale : sales) {
            if (sale.getCustomerId() != null) {
                customerIds.add(sale.getCustomerId());
            }
            if (sale.getAddressId() != null) {
                addressIds.add(sale.getAddressId());
            }
        }
        Map<Integer, Company> customersById = saleRelatedDataSupport.loadCustomersByIds(customerIds);
        Map<Integer, Address> addressesById = saleRelatedDataSupport.loadAddressesByIds(addressIds);

        Set<Integer> employeeIds = new HashSet<>();
        for (SalesJob job : jobsBySaleId.values()) {
            if (job.getAssignedTo() != null) {
                employeeIds.add(job.getAssignedTo());
            }
        }
        Map<Integer, String> employeeNamesById = saleRelatedDataSupport.loadEmployeeNamesByIds(employeeIds);

        List<SaleSummaryResponse> data = sales.stream()
                .map(sale -> toSummary(
                        sale,
                        enrichment.get(sale.getId()),
                        shopifySaleIds.get(sale.getId()),
                        jobsBySaleId.get(sale.getId()),
                        supplierOrdersBySaleId.get(sale.getId()),
                        shipmentsBySaleId.getOrDefault(sale.getId(), List.of()),
                        customersById,
                        addressesById,
                        employeeNamesById
                ))
                .toList();

        int totalPages = safeLimit == 0 ? 0 : (int) Math.ceil((double) total / safeLimit);
        return new PagedSaleListResponse(data, total, safePage, safeLimit, totalPages);
    }

    @Transactional(readOnly = true, transactionManager = "tenantTransactionManager")
    public SaleDetailResponse getSale(Integer id, String referenceNo, String trackingId) {
        Sale sale = resolveSale(id, referenceNo, trackingId);
        return buildSaleDetail(sale, true);
    }

    @Transactional(readOnly = true, transactionManager = "tenantTransactionManager")
    public SaleDetailResponse getSaleWithBatches(Integer id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        return buildSaleDetail(sale, true);
    }

    @Transactional(transactionManager = "tenantTransactionManager")
    public UpdateSaleStatusResponse updateSaleStatus(Integer id, UpdateSaleStatusRequest request) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale with ID " + id + " not found"));

        recordStatusChange(sale, request);

        if (request.saleStatus() != null) {
            sale.setSaleStatus(request.saleStatus());
            SaleStatus.resolveJobType(request.saleStatus()).ifPresent(sale::setJobType);
        }
        if (request.paymentStatus() != null) {
            sale.setPaymentStatus(request.paymentStatus());
        }
        if (request.courierOrderStatus() != null) {
            sale.setCourierOrderStatus(request.courierOrderStatus());
        }
        if (request.jobType() != null) {
            sale.setJobType(request.jobType());
        }
        if (request.changedBy() != null) {
            sale.setUpdatedBy(request.changedBy());
        }

        saleRepository.save(sale);
        return saleMapper.toStatusUpdateResponse(sale);
    }

    @Transactional(transactionManager = "tenantTransactionManager")
    public UpdateShelvingStatusResponse updateShelvingStatus(Integer shelvingItemId, String status) {
        int updated = jdbcTemplate.update(
                "UPDATE sma_purchase_order_shelving_items SET status = ? WHERE id = ?",
                status,
                shelvingItemId
        );
        if (updated == 0) {
            throw new ResourceNotFoundException("Shelving item with ID " + shelvingItemId + " not found");
        }
        return saleMapper.toShelvingStatusResponse(shelvingItemId, status);
    }

    @Transactional(transactionManager = "tenantTransactionManager")
    public SaleDetailResponse editSale(Integer id, EditSaleRequest request) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale " + id + " not found"));

        applyShippingUpdates(sale, request);
        saleRepository.save(sale);

        if (request.hasItems()) {
            upsertSaleItems(sale, request.items());
        }

        log.info("Sale updated id={} referenceNo={}", sale.getId(), sale.getReferenceNo());
        return buildSaleDetail(sale, true);
    }

    private Sale resolveSale(Integer id, String referenceNo, String trackingId) {
        if (id == null && (referenceNo == null || referenceNo.isBlank()) && (trackingId == null || trackingId.isBlank())) {
            throw new BadRequestException("Either id, reference_no, or tracking_id must be provided");
        }

        if (id != null) {
            return saleRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        }
        if (referenceNo != null && !referenceNo.isBlank()) {
            Sale sale = saleQuerySupport.findByReferenceNo(referenceNo);
            if (sale == null) {
                throw new ResourceNotFoundException("Sale not found");
            }
            return sale;
        }

        return saleRepository.findByCourierOrderTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
    }

    private SaleDetailResponse buildSaleDetail(Sale sale, boolean includeBatches) {
        Optional<Company> customer = saleRelatedDataSupport.findCustomer(sale);
        List<SaleItem> saleItems = saleItemRepository.findBySaleId(sale.getId());
        List<String> productCodes = saleItems.stream()
                .map(SaleItem::getProductCode)
                .filter(Objects::nonNull)
                .toList();

        Map<String, BigDecimal> shelvingQtyMap = saleStockSupport.getShelvingQtyMap(productCodes);
        SaleStockSupport.ItemSourceClassification classification = saleStockSupport.classifyItemSources(productCodes);
        Map<String, BigDecimal> pickedQtyMap = saleStockSupport.getPickedQtyMap(sale.getId());
        Map<String, String> productImages = saleRelatedDataSupport.loadProductImages(productCodes);
        Map<String, Integer> supplierIdsByCode = saleRelatedDataSupport.loadSupplierIdsByProductCodes(productCodes);
        Map<String, List<SaleBatchSupport.SaleBatchView>> batchesByProductCode = includeBatches
                ? saleBatchSupport.loadBatchesForProducts(productCodes)
                : Map.of();

        List<SaleDetailResponse.SaleItemDetailResponse> items = saleItems.stream()
                .map(item -> mapItemDetail(
                        item,
                        sale,
                        shelvingQtyMap,
                        classification,
                        pickedQtyMap,
                        productImages,
                        supplierIdsByCode,
                        batchesByProductCode,
                        includeBatches
                ))
                .sorted(Comparator.comparing(this::extractSortKey))
                .toList();

        List<SaleDetailResponse.SaleShipmentDetailResponse> shipments = buildShipmentDetails(sale.getId(), items);
        List<Integer> shippedItemIds = shipments.stream()
                .flatMap(shipment -> shipment.items().stream())
                .map(SaleDetailResponse.ShipmentItemResponse::itemId)
                .distinct()
                .toList();

        String shippingPhone = saleRelatedDataSupport.resolveShippingPhone(sale, customer);
        String customerEmail = saleRelatedDataSupport.resolveCustomerEmail(sale, customer);
        return new SaleDetailResponse(
                sale.getId(),
                saleRelatedDataSupport.resolveShopifySaleId(sale),
                sale.getReferenceNo() == null ? "" : sale.getReferenceNo(),
                sale.getShippingFirstName(),
                sale.getShippingLastName(),
                saleRelatedDataSupport.resolveCustomerName(sale, customer),
                customerEmail,
                shippingPhone == null ? "N/A" : shippingPhone,
                sale.getDate(),
                sale.getJobType() == null ? "" : sale.getJobType(),
                SaleStatus.toDisplayName(sale.getSaleStatus()),
                sale.getPaymentStatus() == null ? "pending" : sale.getPaymentStatus(),
                sale.getCourierLabel(),
                sale.getCourierOrderStatus(),
                sale.getCourierOrderTrackingId(),
                sale.getShippingAddress1(),
                sale.getShippingAddress2(),
                sale.getShippingCity(),
                sale.getShippingZip(),
                sale.getShippingCountry(),
                saleRelatedDataSupport.toJobResponse(saleRelatedDataSupport.findJobForSale(sale.getId()).orElse(null)),
                items,
                shippedItemIds,
                shipments.size(),
                shipments,
                loadReturnedQuantity(sale),
                items.size(),
                defaultDecimal(sale.getGrandTotal()),
                defaultDecimal(sale.getTotalDiscount()),
                defaultDecimal(sale.getShipping()),
                parseDecimal(sale.getShippingLatitude()),
                parseDecimal(sale.getShippingLongitude()),
                defaultDecimal(sale.getTotalTax()),
                defaultDecimal(sale.getProductTax()),
                defaultDecimal(sale.getOrderTax()),
                sale.getNote(),
                sale.getStaffNote(),
                sale.getWarehouseId(),
                sale.getPaymentMethod(),
                sale.getSourceName(),
                sale.getShopifyTags()
        );
    }

    private SaleDetailResponse.SaleItemDetailResponse mapItemDetail(
            SaleItem item,
            Sale sale,
            Map<String, BigDecimal> shelvingQtyMap,
            SaleStockSupport.ItemSourceClassification classification,
            Map<String, BigDecimal> pickedQtyMap,
            Map<String, String> productImages,
            Map<String, Integer> supplierIdsByCode,
            Map<String, List<SaleBatchSupport.SaleBatchView>> batchesByProductCode,
            boolean includeBatches
    ) {
        String normalized = ProductCodeNormalizer.normalize(item.getProductCode());
        BigDecimal shelvingQty = shelvingQtyMap.getOrDefault(normalized, BigDecimal.ZERO);
        boolean isShelved = classification.shelvedCodes().contains(normalized);
        boolean hasExternal = classification.externalCodes().contains(normalized);
        String itemSource = saleStockSupport.resolveItemStockSource(
                defaultDecimal(item.getQuantity()),
                shelvingQty,
                isShelved,
                hasExternal
        );

        List<SaleDetailResponse.SaleItemBatchResponse> batches = List.of();
        if (includeBatches && item.getProductCode() != null) {
            List<SaleBatchSupport.SaleBatchView> productBatches =
                    batchesByProductCode.getOrDefault(normalized, List.of());
            batches = productBatches.stream()
                    .map(batch -> new SaleDetailResponse.SaleItemBatchResponse(
                            batch.batchNo(),
                            batch.expiryDate(),
                            batch.totalQty(),
                            batch.shelvingIdBoxQty(),
                            batch.locationStatus().stream()
                                    .map(loc -> new SaleDetailResponse.LocationStatusResponse(loc.box(), loc.status()))
                                    .toList()
                    ))
                    .toList();
        }

        String productImage = null;
        if (item.getProductCode() != null) {
            productImage = productImages.get(item.getProductCode().trim());
            if (productImage == null) {
                productImage = productImages.get(normalized);
            }
        }

        Integer supplierId = 0;
        if (item.getProductCode() != null) {
            supplierId = supplierIdsByCode.getOrDefault(item.getProductCode().trim(),
                    supplierIdsByCode.getOrDefault(normalized, 0));
        }

        return new SaleDetailResponse.SaleItemDetailResponse(
                item.getId(),
                item.getId(),
                item.getProductId(),
                supplierId,
                item.getProductCode() == null ? "" : item.getProductCode(),
                item.getProductName(),
                defaultDecimal(item.getQuantity()),
                sale.getJobType() == null ? "" : sale.getJobType(),
                defaultDecimal(item.getUnitPrice()),
                defaultDecimal(item.getNetUnitPrice()),
                defaultDecimal(item.getSubtotal()),
                item.getProductType(),
                productImage,
                item.getWarehouseId(),
                defaultDecimal(item.getItemDiscount()),
                defaultDecimal(item.getItemTax()),
                itemSource,
                shelvingQty,
                null,
                null,
                null,
                null,
                item.getActive() == null ? Boolean.TRUE : item.getActive(),
                pickedQtyMap.getOrDefault(normalized, BigDecimal.ZERO),
                batches
        );
    }

    private String extractSortKey(SaleDetailResponse.SaleItemDetailResponse item) {
        List<SaleBatchSupport.SaleBatchView> batches = item.batches().stream()
                .map(batch -> new SaleBatchSupport.SaleBatchView(
                        batch.batchNo(),
                        batch.expiryDate(),
                        batch.totalQty(),
                        batch.shelvingIdBoxQty(),
                        batch.locationStatus().stream()
                                .map(loc -> new SaleBatchSupport.LocationStatusView(loc.box(), loc.status()))
                                .toList()
                ))
                .toList();
        return saleBatchSupport.extractSortBoxKey(batches);
    }

    private SaleSummaryResponse toSummary(
            Sale sale,
            Map<String, Object> enrichment,
            String shopifySaleId,
            SalesJob job,
            SupplierOrder supplierOrder,
            List<SaleSummaryResponse.SaleShipmentSummaryResponse> shipments,
            Map<Integer, Company> customersById,
            Map<Integer, Address> addressesById,
            Map<Integer, String> employeeNamesById
    ) {
        Optional<Company> customer = sale.getCustomerId() == null
                ? Optional.empty()
                : Optional.ofNullable(customersById.get(sale.getCustomerId()));
        Optional<Address> address = sale.getAddressId() == null
                ? Optional.empty()
                : Optional.ofNullable(addressesById.get(sale.getAddressId()));
        int totalItems = sale.getTotalItems() == null ? 0 : sale.getTotalItems();
        if (enrichment != null && enrichment.get("items_count") instanceof Number count) {
            totalItems = count.intValue();
        }

        String phone = saleRelatedDataSupport.resolveShippingPhone(sale, customer, address);
        String customerEmail = saleRelatedDataSupport.resolveCustomerEmail(sale, customer);
        return new SaleSummaryResponse(
                sale.getId(),
                shopifySaleId,
                supplierOrder == null ? null : supplierOrder.getStatus(),
                supplierOrder == null ? null : supplierOrder.getSupplierId(),
                sale.getReferenceNo() == null ? "#" + sale.getId() : sale.getReferenceNo(),
                sale.getShippingFirstName(),
                sale.getShippingLastName(),
                saleRelatedDataSupport.resolveCustomerName(sale, customer),
                customerEmail,
                phone == null ? "N/A" : phone,
                sale.getDate(),
                sale.getJobType() == null ? "" : sale.getJobType(),
                SaleStatus.toDisplayName(sale.getSaleStatus()),
                sale.getPaymentStatus() == null ? "pending" : sale.getPaymentStatus(),
                sale.getCourierOrderTrackingId(),
                sale.getSourceName(),
                sale.getShopifyTags(),
                totalItems,
                saleRelatedDataSupport.toJobResponse(job, employeeNamesById),
                shipments
        );
    }

    private void recordStatusChange(Sale sale, UpdateSaleStatusRequest request) {
        if (!request.hasSaleStatus() || Objects.equals(request.saleStatus(), sale.getSaleStatus())) {
            return;
        }

        SaleStatusHistory history = new SaleStatusHistory();
        history.setSaleId(sale.getId());
        history.setOldStatus(sale.getSaleStatus());
        history.setNewStatus(request.saleStatus());
        history.setChangedBy(request.changedBy());
        history.setNote(request.note());
        history.setChangedAt(LocalDateTime.now());
        saleStatusHistoryRepository.save(history);
    }

    private void applyShippingUpdates(Sale sale, EditSaleRequest request) {
        if (request.shippingFirstName() != null) {
            sale.setShippingFirstName(request.shippingFirstName());
        }
        if (request.shippingLastName() != null) {
            sale.setShippingLastName(request.shippingLastName());
        }
        if (request.shippingPhone() != null) {
            sale.setShippingPhone(request.shippingPhone());
        }
        if (request.shippingAddress1() != null) {
            sale.setShippingAddress1(request.shippingAddress1());
        }
        if (request.shippingAddress2() != null) {
            sale.setShippingAddress2(request.shippingAddress2());
        }
        if (request.shippingCity() != null) {
            sale.setShippingCity(request.shippingCity());
        }
        if (request.shippingZip() != null) {
            sale.setShippingZip(request.shippingZip());
        }
        if (request.shippingCountry() != null) {
            sale.setShippingCountry(request.shippingCountry());
        }
        if (request.note() != null) {
            sale.setNote(request.note());
        }
        if (request.staffNote() != null) {
            sale.setStaffNote(request.staffNote());
        }
    }

    private void upsertSaleItems(Sale sale, List<EditSaleRequest.EditSaleItemRequest> items) {
        List<String> productCodes = items.stream()
                .map(item -> item.productCode().trim())
                .distinct()
                .toList();

        Map<String, SaleItem> existingByCode = saleItemRepository
                .findBySaleIdAndProductCodeIn(sale.getId(), productCodes)
                .stream()
                .collect(Collectors.toMap(item -> item.getProductCode().trim(), item -> item, (a, b) -> a));

        List<SaleItem> toSave = new ArrayList<>();
        for (EditSaleRequest.EditSaleItemRequest itemRequest : items) {
            String code = itemRequest.productCode().trim();
            SaleItem existing = existingByCode.get(code);
            if (existing != null) {
                BigDecimal newQty = defaultDecimal(existing.getQuantity()).add(BigDecimal.valueOf(itemRequest.quantity()));
                existing.setQuantity(newQty);
                existing.setSubtotal(defaultDecimal(existing.getUnitPrice()).multiply(newQty));
                toSave.add(existing);
                continue;
            }

            SaleItem newItem = new SaleItem();
            newItem.setSaleId(sale.getId());
            newItem.setProductCode(code);
            newItem.setProductName(itemRequest.productName() == null ? code : itemRequest.productName());
            newItem.setProductType("standard");
            newItem.setQuantity(BigDecimal.valueOf(itemRequest.quantity()));
            newItem.setUnitPrice(BigDecimal.ZERO);
            newItem.setNetUnitPrice(BigDecimal.ZERO);
            newItem.setSubtotal(BigDecimal.ZERO);
            newItem.setWarehouseId(sale.getWarehouseId());
            toSave.add(newItem);
        }

        saleItemRepository.saveAll(toSave);
    }

    private List<SaleDetailResponse.ReturnedQuantityResponse> loadReturnedQuantity(Sale sale) {
        String status = sale.getSaleStatus();
        if (status == null || (!status.equalsIgnoreCase("cancelled") && !status.equalsIgnoreCase("returned"))) {
            return null;
        }

        String sql = """
                SELECT b.id AS shelving_id, c.product_code, c.qty, c.status, si.product_name
                FROM sma_sales a
                JOIN sma_purchase_order_shelving b ON b.sale_id = a.id
                JOIN sma_purchase_order_shelving_items c ON c.shelving_id = b.id
                LEFT JOIN sma_sale_items si ON si.sale_id = a.id
                  AND TRIM(LEADING '0' FROM si.product_code) = TRIM(LEADING '0' FROM c.product_code)
                WHERE a.id = ? AND a.sale_status = ? AND c.status = ?
                GROUP BY c.product_code
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new SaleDetailResponse.ReturnedQuantityResponse(
                rs.getInt("shelving_id"),
                rs.getString("product_code"),
                rs.getString("product_name"),
                rs.getBigDecimal("qty"),
                rs.getString("status")
        ), sale.getId(), sale.getSaleStatus(), "returned");
    }

    private List<SaleDetailResponse.SaleShipmentDetailResponse> buildShipmentDetails(
            Integer saleId,
            List<SaleDetailResponse.SaleItemDetailResponse> items
    ) {
        List<ShipmentCreationLog> logs = saleRelatedDataSupport.loadShipments(saleId);
        List<SaleRelatedDataSupport.ShipmentItemRow> shipmentItems = saleRelatedDataSupport.loadShipmentItems(saleId);

        Map<Integer, String> itemNamesById = items.stream()
                .collect(Collectors.toMap(
                        SaleDetailResponse.SaleItemDetailResponse::id,
                        SaleDetailResponse.SaleItemDetailResponse::productName,
                        (left, right) -> left
                ));
        Map<String, String> itemNamesByCode = items.stream()
                .collect(Collectors.toMap(
                        SaleDetailResponse.SaleItemDetailResponse::productCode,
                        SaleDetailResponse.SaleItemDetailResponse::productName,
                        (left, right) -> left
                ));

        Map<String, List<SaleDetailResponse.ShipmentItemResponse>> itemsByTracking = new java.util.HashMap<>();
        for (SaleRelatedDataSupport.ShipmentItemRow row : shipmentItems) {
            if (row.trackingId() == null) {
                continue;
            }
            itemsByTracking.computeIfAbsent(row.trackingId(), key -> new ArrayList<>())
                    .add(new SaleDetailResponse.ShipmentItemResponse(
                            row.id(),
                            row.itemId(),
                            row.productCode(),
                            itemNamesById.getOrDefault(row.itemId(), itemNamesByCode.get(row.productCode())),
                            defaultDecimal(row.qty())
                    ));
        }

        List<SaleDetailResponse.SaleShipmentDetailResponse> shipments = new ArrayList<>();
        for (ShipmentCreationLog logEntry : logs) {
            String status = logEntry.getStatus() == null ? "created" : logEntry.getStatus();
            shipments.add(new SaleDetailResponse.SaleShipmentDetailResponse(
                    logEntry.getId(),
                    logEntry.getTrackingNumber(),
                    logEntry.getProvider(),
                    status,
                    logEntry.getLabelUrl(),
                    logEntry.getNumberOfPieces(),
                    logEntry.getCreatedAt(),
                    itemsByTracking.getOrDefault(logEntry.getTrackingNumber(), List.of())
            ));
        }
        return shipments;
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }
}
