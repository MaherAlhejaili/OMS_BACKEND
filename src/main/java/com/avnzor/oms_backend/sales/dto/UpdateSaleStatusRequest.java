package com.avnzor.oms_backend.sales.dto;

import jakarta.validation.constraints.AssertTrue;

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

    @AssertTrue(message = "At least one of saleStatus, paymentStatus, courierOrderStatus, or jobType must be provided")
    public boolean hasAtLeastOneStatusField() {
        return hasSaleStatus()
                || isNotBlank(paymentStatus)
                || isNotBlank(courierOrderStatus)
                || isNotBlank(jobType);
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
