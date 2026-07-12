package com.avnzor.oms_backend.tenants.exception;

import com.avnzor.oms_backend.common.exception.BadRequestException;

public class TenantContextMissingException extends BadRequestException {

    public TenantContextMissingException() {
        super("Tenant context is required for this operation");
    }
}
