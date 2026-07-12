package com.avnzor.oms_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String tenantSlug,
        @NotBlank String username,
        @NotBlank String password
) {
}
