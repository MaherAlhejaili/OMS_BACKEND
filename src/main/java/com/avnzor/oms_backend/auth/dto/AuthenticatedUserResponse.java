package com.avnzor.oms_backend.auth.dto;

public record AuthenticatedUserResponse(
        Long id,
        String username,
        String name,
        String role,
        String department,
        Long tenantId,
        String tenantSlug
) {
}
