package com.avnzor.oms_backend.auth.dto;

public record AuthenticatedUserResponse(
        Integer id,
        String username,
        String name,
        String role,
        String department
) {
}
