package com.avnzor.oms_backend.auth.service;

import com.avnzor.oms_backend.auth.config.JwtProperties;
import com.avnzor.oms_backend.support.TestUserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtService unit tests")
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(
                "test-jwt-secret-at-least-32-characters-long",
                3_600_000L
        );
        jwtService = new JwtService(properties);
    }

    @Test
    @DisplayName("Given tenant principal When generating token Then token contains tenant claims")
    void shouldGenerateTenantTokenWithClaims() {
        String token = jwtService.generateTenantToken(TestUserFactory.logisticPrincipal());

        assertThat(jwtService.isTenantToken(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("logistic.user");
        assertThat(jwtService.extractTenantSlug(token)).isEqualTo(TestUserFactory.TEST_TENANT_SLUG);
        assertThat(jwtService.extractTenantId(token)).isEqualTo(TestUserFactory.TEST_TENANT_ID);
        assertThat(jwtService.isTenantTokenValid(token, TestUserFactory.logisticPrincipal())).isTrue();
    }

    @Test
    @DisplayName("Given token for different tenant When validating Then token is invalid")
    void shouldRejectTokenForDifferentTenant() {
        String token = jwtService.generateTenantToken(TestUserFactory.logisticPrincipal());
        var otherTenantPrincipal = new com.avnzor.oms_backend.auth.security.WarehouseUserPrincipal(
                TestUserFactory.logisticWorker(),
                99L,
                "other-tenant"
        );

        assertThat(jwtService.isTenantTokenValid(token, otherTenantPrincipal)).isFalse();
    }
}
