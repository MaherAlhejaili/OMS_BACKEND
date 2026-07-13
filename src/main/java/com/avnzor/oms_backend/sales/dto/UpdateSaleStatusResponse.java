package com.avnzor.oms_backend.sales.dto;

public record UpdateSaleStatusResponse(
        String message,
        Integer id,
        String saleStatus,
        String paymentStatus,
        String courierOrderStatus,
        String jobType
) {
}
