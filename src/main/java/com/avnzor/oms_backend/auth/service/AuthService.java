package com.avnzor.oms_backend.auth.service;

import com.avnzor.oms_backend.auth.dto.AuthenticatedUserResponse;
import com.avnzor.oms_backend.auth.dto.LoginRequest;
import com.avnzor.oms_backend.auth.dto.LoginResponse;
import com.avnzor.oms_backend.auth.entity.WarehouseWorker;
import com.avnzor.oms_backend.auth.repository.WarehouseWorkerRepository;
import com.avnzor.oms_backend.auth.security.WarehouseUserPrincipal;
import com.avnzor.oms_backend.common.exception.UnauthorizedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final WarehouseWorkerRepository warehouseWorkerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            WarehouseWorkerRepository warehouseWorkerRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.warehouseWorkerRepository = warehouseWorkerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        WarehouseWorker worker = warehouseWorkerRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordMatches(request.password(), worker.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        WarehouseUserPrincipal principal = new WarehouseUserPrincipal(worker);
        String accessToken = jwtService.generateToken(principal);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getExpirationSeconds(),
                toUserResponse(worker)
        );
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }

        // Supports legacy plain-text passwords and BCrypt hashes from newer records.
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }

        return storedPassword.equals(rawPassword);
    }

    private AuthenticatedUserResponse toUserResponse(WarehouseWorker worker) {
        return new AuthenticatedUserResponse(
                worker.getId(),
                worker.getUsername(),
                worker.getName(),
                worker.getRole(),
                worker.getDepartment()
        );
    }
}
