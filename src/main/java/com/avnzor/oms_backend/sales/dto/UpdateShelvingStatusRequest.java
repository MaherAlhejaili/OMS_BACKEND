package com.avnzor.oms_backend.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateShelvingStatusRequest(
        @NotBlank String status
) {
}
