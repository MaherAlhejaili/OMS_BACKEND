package com.avnzor.oms_backend.tenants.context;

public record TenantContext(
        Long tenantId,
        String tenantSlug
) {
}
