package com.avnzor.oms_backend.sales.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleDetailResponse(
        Integer id,
        String shopifySaleId,
        String referenceNo,
        String shippingFirstName,
        String shippingLastName,
        String customer,
        String shippingPhone,
        LocalDateTime date,
        String jobType,
        String saleStatus,
        String paymentStatus,
        String courierLabel,
        String courierOrderStatus,
        String courierOrderTrackingId,
        String shippingAddress1,
        String shippingAddress2,
        String shippingCity,
        String shippingPostcode,
        String shippingCountry,
        SaleSummaryResponse.SaleJobResponse job,
        List<SaleItemDetailResponse> items,
        List<Integer> shippedItemIds,
        int shipmentCount,
        List<SaleShipmentDetailResponse> shipments,
        List<ReturnedQuantityResponse> returnedQuantity,
        int totalItems,
        BigDecimal grandTotal,
        BigDecimal totalDiscount,
        BigDecimal shipping,
        BigDecimal shippingLatitude,
        BigDecimal shippingLongitude,
        BigDecimal totalTax,
        BigDecimal productTax,
        BigDecimal orderTax,
        String note,
        String staffNote,
        Integer warehouseId,
        String paymentMethod,
        String sourceName,
        String shopifyTags
) {
    public record SaleItemDetailResponse(
            Integer id,
            Integer saleItemId,
            Integer productId,
            Integer supplierId,
            String productCode,
            String productName,
            BigDecimal quantity,
            String jobType,
            BigDecimal unitPrice,
            BigDecimal netUnitPrice,
            BigDecimal subtotal,
            String productType,
            String productImage,
            Integer warehouseId,
            BigDecimal itemDiscount,
            BigDecimal itemTax,
            String itemSource,
            BigDecimal shelvingQty,
            ExternalSupplierResponse externalSupplier,
            Integer returnSuggestedShelvingId,
            String returnSuggestedBoxNumber,
            String returnSuggestedBatchNo,
            Boolean active,
            BigDecimal pickedQty,
            List<SaleItemBatchResponse> batches
    ) {
    }

    public record ExternalSupplierResponse(
            Integer supplierId,
            BigDecimal quantity,
            Integer itemId,
            String supplierName
    ) {
    }

    public record SaleItemBatchResponse(
            String batchNo,
            String expiryDate,
            BigDecimal totalQty,
            String shelvingIdBoxQty,
            List<LocationStatusResponse> locationStatus
    ) {
    }

    public record LocationStatusResponse(
            String box,
            String status
    ) {
    }

    public record SaleShipmentDetailResponse(
            Integer id,
            String trackingId,
            String carrier,
            String status,
            String labelUrl,
            Integer numberOfPieces,
            LocalDateTime createdAt,
            List<ShipmentItemResponse> items
    ) {
    }

    public record ShipmentItemResponse(
            Integer id,
            Integer itemId,
            String productCode,
            String productName,
            BigDecimal qty
    ) {
    }

    public record ReturnedQuantityResponse(
            Integer shelvingId,
            String productCode,
            String productName,
            BigDecimal qty,
            String status
    ) {
    }
}
