package com.avnzor.oms_backend.tenants.dto;

import com.avnzor.oms_backend.tenants.entity.TenantStatus;

import java.time.Instant;

public record TenantResponse(
        Long id,
        String slug,
        String name,
        TenantStatus status,
        String databaseHost,
        Integer databasePort,
        String databaseName,
        Instant createdAt
) {
}
