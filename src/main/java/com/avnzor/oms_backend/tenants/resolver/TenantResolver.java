package com.avnzor.oms_backend.tenants.resolver;

import com.avnzor.oms_backend.auth.service.JwtService;
import com.avnzor.oms_backend.tenants.context.TenantContext;
import com.avnzor.oms_backend.tenants.context.TenantContextHolder;
import com.avnzor.oms_backend.tenants.entity.Tenant;
import com.avnzor.oms_backend.tenants.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantResolver {

    private final TenantService tenantService;
    private final JwtService jwtService;

    public void resolveFromJwt(String token) {
        String tenantSlug = jwtService.extractTenantSlug(token);
        Long tenantId = jwtService.extractTenantId(token);

        Tenant tenant = tenantService.resolveActiveTenant(tenantSlug);
        if (!tenant.getId().equals(tenantId)) {
            throw new com.avnzor.oms_backend.common.exception.UnauthorizedException("Invalid tenant in token");
        }

        TenantContext context = tenantService.toContext(tenant);
        TenantContextHolder.set(context);
        log.debug("Tenant context set from JWT slug={}", tenantSlug);
    }

    public void resolveForLogin(String tenantSlug) {
        Tenant tenant = tenantService.resolveActiveTenant(tenantSlug);
        TenantContextHolder.set(tenantService.toContext(tenant));
        log.debug("Tenant context set for login slug={}", tenantSlug);
    }
}
