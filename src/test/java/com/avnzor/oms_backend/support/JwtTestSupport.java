package com.avnzor.oms_backend.support;

import com.avnzor.oms_backend.auth.security.WarehouseUserPrincipal;
import com.avnzor.oms_backend.auth.service.JwtService;
import com.avnzor.oms_backend.tenants.security.PlatformUserPrincipal;

public final class JwtTestSupport {

    private JwtTestSupport() {
    }

    public static String bearerToken(JwtService jwtService, WarehouseUserPrincipal principal) {
        return "Bearer " + jwtService.generateTenantToken(principal);
    }

    public static String platformBearerToken(JwtService jwtService, PlatformUserPrincipal principal) {
        return "Bearer " + jwtService.generatePlatformToken(principal);
    }
}
