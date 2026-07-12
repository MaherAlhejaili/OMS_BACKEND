package com.avnzor.oms_backend.auth.service;

import com.avnzor.oms_backend.auth.dto.AuthenticatedUserResponse;
import com.avnzor.oms_backend.auth.dto.LoginRequest;
import com.avnzor.oms_backend.auth.dto.LoginResponse;
import com.avnzor.oms_backend.auth.entity.WarehouseWorker;
import com.avnzor.oms_backend.auth.repository.WarehouseWorkerRepository;
import com.avnzor.oms_backend.auth.security.WarehouseUserPrincipal;
import com.avnzor.oms_backend.common.exception.UnauthorizedException;
import com.avnzor.oms_backend.tenants.context.TenantContextHolder;
import com.avnzor.oms_backend.tenants.resolver.TenantResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final WarehouseWorkerRepository warehouseWorkerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TenantResolver tenantResolver;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        tenantResolver.resolveForLogin(request.tenantSlug());

        WarehouseWorker worker = warehouseWorkerRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordMatches(request.password(), worker.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        Long tenantId = TenantContextHolder.getTenantId();
        String tenantSlug = TenantContextHolder.getTenantSlug();

        WarehouseUserPrincipal principal = new WarehouseUserPrincipal(worker, tenantId, tenantSlug);
        String accessToken = jwtService.generateTenantToken(principal);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getExpirationSeconds(),
                toUserResponse(worker, tenantId, tenantSlug)
        );
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }

        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }

        return storedPassword.equals(rawPassword);
    }

    private AuthenticatedUserResponse toUserResponse(WarehouseWorker worker, Long tenantId, String tenantSlug) {
        return new AuthenticatedUserResponse(
                worker.getId(),
                worker.getUsername(),
                worker.getName(),
                worker.getRole(),
                worker.getDepartment(),
                tenantId,
                tenantSlug
        );
    }
}
