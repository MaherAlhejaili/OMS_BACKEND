package com.avnzor.oms_backend.sales.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SaleSummaryResponse(
        Integer id,
        String shopifySaleId,
        String supplierStatus,
        Integer supplierId,
        String referenceNo,
        String shippingFirstName,
        String shippingLastName,
        String customer,
        String shippingPhone,
        LocalDateTime date,
        String jobType,
        String saleStatus,
        String paymentStatus,
        String courierOrderTrackingId,
        String sourceName,
        String shopifyTags,
        int totalItems,
        SaleJobResponse job,
        List<SaleShipmentSummaryResponse> shipments
) {
    public record SaleJobResponse(
            Integer id,
            Integer assignedTo,
            Integer assignedBy,
            LocalDateTime createdAt,
            String employeeName
    ) {
    }

    public record SaleShipmentSummaryResponse(
            String trackingId,
            String carrier,
            String status
    ) {
    }
}
