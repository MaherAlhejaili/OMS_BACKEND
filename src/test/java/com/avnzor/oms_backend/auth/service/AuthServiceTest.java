package com.avnzor.oms_backend.auth.service;

import com.avnzor.oms_backend.auth.config.AuthProperties;
import com.avnzor.oms_backend.auth.config.JwtProperties;
import com.avnzor.oms_backend.auth.dto.LoginRequest;
import com.avnzor.oms_backend.auth.entity.WarehouseWorker;
import com.avnzor.oms_backend.auth.repository.WarehouseWorkerRepository;
import com.avnzor.oms_backend.common.exception.UnauthorizedException;
import com.avnzor.oms_backend.support.TestUserFactory;
import com.avnzor.oms_backend.tenants.context.TenantContextHolder;
import com.avnzor.oms_backend.tenants.resolver.TenantResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService unit tests")
class AuthServiceTest {

    @Mock
    private WarehouseWorkerRepository warehouseWorkerRepository;

    @Mock
    private TenantResolver tenantResolver;

    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        jwtService = new JwtService(new JwtProperties(
                "test-jwt-secret-at-least-32-characters-long",
                3_600_000L
        ));
        authService = new AuthService(
                warehouseWorkerRepository,
                passwordEncoder,
                jwtService,
                tenantResolver,
                new AuthProperties(false)
        );

        doAnswer(invocation -> {
            TenantContextHolder.set(TestUserFactory.TEST_TENANT_ID, TestUserFactory.TEST_TENANT_SLUG);
            return null;
        }).when(tenantResolver).resolveForLogin(TestUserFactory.TEST_TENANT_SLUG);
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    @DisplayName("Given BCrypt password When logging in Then authentication succeeds")
    void shouldAuthenticateBcryptPassword() {
        WarehouseWorker worker = TestUserFactory.logisticWorker();
        worker.setPassword(passwordEncoder.encode("secret"));

        when(warehouseWorkerRepository.findByUsername("logistic.user")).thenReturn(Optional.of(worker));

        var response = authService.login(new LoginRequest(
                TestUserFactory.TEST_TENANT_SLUG,
                "logistic.user",
                "secret"
        ));

        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.user().username()).isEqualTo("logistic.user");
    }

    @Test
    @DisplayName("Given plain-text password and legacy disabled When logging in Then authentication fails")
    void shouldRejectPlainTextPasswordWhenLegacyDisabled() {
        WarehouseWorker worker = TestUserFactory.logisticWorker();
        worker.setPassword("plain-password");

        when(warehouseWorkerRepository.findByUsername("logistic.user")).thenReturn(Optional.of(worker));

        assertThatThrownBy(() -> authService.login(new LoginRequest(
                TestUserFactory.TEST_TENANT_SLUG,
                "logistic.user",
                "plain-password"
        ))).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("Given plain-text password and legacy enabled When logging in Then authentication succeeds")
    void shouldAllowPlainTextPasswordWhenLegacyEnabled() {
        WarehouseWorker worker = TestUserFactory.logisticWorker();
        worker.setPassword("password");

        when(warehouseWorkerRepository.findByUsername("logistic.user")).thenReturn(Optional.of(worker));

        authService = new AuthService(
                warehouseWorkerRepository,
                passwordEncoder,
                jwtService,
                tenantResolver,
                new AuthProperties(true)
        );

        var response = authService.login(new LoginRequest(
                TestUserFactory.TEST_TENANT_SLUG,
                "logistic.user",
                "password"
        ));

        assertThat(response.accessToken()).isNotBlank();
    }
}
