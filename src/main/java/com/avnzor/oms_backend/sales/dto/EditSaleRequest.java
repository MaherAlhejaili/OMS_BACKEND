package com.avnzor.oms_backend.sales.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record EditSaleRequest(
        String shippingFirstName,
        String shippingLastName,
        String shippingPhone,
        String shippingAddress1,
        String shippingAddress2,
        String shippingCity,
        String shippingZip,
        String shippingCountry,
        String note,
        String staffNote,
        @Valid List<EditSaleItemRequest> items
) {
    public record EditSaleItemRequest(
            @NotBlank String productCode,
            String productName,
            @NotNull @Positive Integer quantity
    ) {
    }

    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }
}
