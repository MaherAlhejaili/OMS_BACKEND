package com.avnzor.oms_backend.support;

import com.avnzor.oms_backend.auth.security.WarehouseUserPrincipal;
import com.avnzor.oms_backend.auth.service.JwtService;

public final class JwtTestSupport {

    private JwtTestSupport() {
    }

    public static String bearerToken(JwtService jwtService, WarehouseUserPrincipal principal) {
        return "Bearer " + jwtService.generateTenantToken(principal);
    }
}
