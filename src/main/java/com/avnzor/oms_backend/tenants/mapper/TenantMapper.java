package com.avnzor.oms_backend.tenants.mapper;

import com.avnzor.oms_backend.tenants.dto.TenantResponse;
import com.avnzor.oms_backend.tenants.entity.Tenant;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class TenantMapper {

    public TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getSlug(),
                tenant.getName(),
                tenant.getStatus(),
                tenant.getDatabaseHost(),
                tenant.getDatabasePort(),
                tenant.getDatabaseName(),
                tenant.getCreatedAt() == null ? null : tenant.getCreatedAt().toInstant(ZoneOffset.UTC)
        );
    }
}
