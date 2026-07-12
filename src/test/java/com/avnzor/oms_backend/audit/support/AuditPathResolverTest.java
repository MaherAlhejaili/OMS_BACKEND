package com.avnzor.oms_backend.audit.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuditPathResolver unit tests")
class AuditPathResolverTest {

    @Test
    @DisplayName("Given orders path with id When resolving Then returns orders and id")
    void shouldResolveOrdersPathWithId() {
        AuditPathResolver.ResolvedPath resolved = AuditPathResolver.resolve("/api/v1/orders/42");

        assertThat(resolved.entityType()).isEqualTo("orders");
        assertThat(resolved.entityId()).isEqualTo("42");
    }

    @Test
    @DisplayName("Given auth login path When resolving Then returns auth and login segment")
    void shouldResolveAuthPathWithoutId() {
        AuditPathResolver.ResolvedPath resolved = AuditPathResolver.resolve("/api/v1/auth/login");

        assertThat(resolved.entityType()).isEqualTo("auth");
        assertThat(resolved.entityId()).isEqualTo("login");
    }

    @Test
    @DisplayName("Given blank path When resolving Then returns api fallback")
    void shouldReturnApiFallbackForBlankPath() {
        AuditPathResolver.ResolvedPath resolved = AuditPathResolver.resolve("  ");

        assertThat(resolved.entityType()).isEqualTo("api");
        assertThat(resolved.entityId()).isNull();
    }
}
