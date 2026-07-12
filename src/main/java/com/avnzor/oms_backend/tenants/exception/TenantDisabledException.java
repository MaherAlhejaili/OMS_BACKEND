package com.avnzor.oms_backend.tenants.exception;

import com.avnzor.oms_backend.common.exception.BadRequestException;

public class TenantDisabledException extends BadRequestException {

    public TenantDisabledException(String tenantSlug) {
        super("Tenant is disabled: " + tenantSlug);
    }
}
