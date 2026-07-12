package com.avnzor.oms_backend.tenants.dto;

public record PlatformLoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String username,
        String name,
        String role
) {
}
