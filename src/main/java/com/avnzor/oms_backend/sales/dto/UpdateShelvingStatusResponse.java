package com.avnzor.oms_backend.sales.dto;

public record UpdateShelvingStatusResponse(
        boolean success,
        Integer id,
        String status
) {
}
