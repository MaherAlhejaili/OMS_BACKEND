package com.avnzor.oms_backend.tenants.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record CreateTenantRequest(
        @NotBlank @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*") String slug,
        @NotBlank String name,
        @NotBlank String databaseHost,
        @NotNull @Positive Integer databasePort,
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_]+") String databaseName,
        @NotBlank String databaseUsername,
        @NotBlank String databasePassword
) {
}
