package com.avnzor.oms_backend.tenants.exception;

import com.avnzor.oms_backend.common.exception.BadRequestException;

public class TenantNotFoundException extends BadRequestException {

    public TenantNotFoundException(String tenantSlug) {
        super("Tenant not found: " + tenantSlug);
    }
}
