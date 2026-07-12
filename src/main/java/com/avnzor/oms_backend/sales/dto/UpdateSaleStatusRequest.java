package com.avnzor.oms_backend.sales.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateSaleStatusRequest(
        String saleStatus,
        String paymentStatus,
        String courierOrderStatus,
        String jobType,
        Integer changedBy,
        String note
) {
    public boolean hasSaleStatus() {
        return saleStatus != null && !saleStatus.isBlank();
    }
}
