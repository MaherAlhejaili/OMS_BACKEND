package com.avnzor.oms_backend.tenants.service;

import com.avnzor.oms_backend.auth.service.JwtService;
import com.avnzor.oms_backend.common.exception.UnauthorizedException;
import com.avnzor.oms_backend.tenants.dto.PlatformLoginRequest;
import com.avnzor.oms_backend.tenants.dto.PlatformLoginResponse;
import com.avnzor.oms_backend.tenants.entity.PlatformUser;
import com.avnzor.oms_backend.tenants.repository.PlatformUserRepository;
import com.avnzor.oms_backend.tenants.security.PlatformUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlatformAuthService {

    private final PlatformUserRepository platformUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(value = "platformTransactionManager", readOnly = true)
    public PlatformLoginResponse login(PlatformLoginRequest request) {
        PlatformUser user = platformUserRepository.findByUsername(request.username())
                .filter(PlatformUser::isActive)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        PlatformUserPrincipal principal = new PlatformUserPrincipal(user);
        String accessToken = jwtService.generatePlatformToken(principal);

        return new PlatformLoginResponse(
                accessToken,
                "Bearer",
                jwtService.getExpirationSeconds(),
                user.getUsername(),
                user.getName(),
                user.getRole()
        );
    }
}
