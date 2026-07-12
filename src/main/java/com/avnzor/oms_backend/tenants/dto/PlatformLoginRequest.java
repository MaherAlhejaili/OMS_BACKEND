package com.avnzor.oms_backend.tenants.dto;

import jakarta.validation.constraints.NotBlank;

public record PlatformLoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
