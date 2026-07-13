package com.avnzor.oms_backend.tenants.controller;

import com.avnzor.oms_backend.tenants.dto.CreateTenantRequest;
import com.avnzor.oms_backend.tenants.dto.PlatformLoginRequest;
import com.avnzor.oms_backend.tenants.dto.PlatformLoginResponse;
import com.avnzor.oms_backend.tenants.dto.TenantResponse;
import com.avnzor.oms_backend.tenants.service.PlatformAuthService;
import com.avnzor.oms_backend.tenants.service.TenantProvisioningService;
import com.avnzor.oms_backend.tenants.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/platform")
@Tag(name = "Platform", description = "Platform administration and tenant management")
@RequiredArgsConstructor
public class TenantController {

    private final PlatformAuthService platformAuthService;
    private final TenantProvisioningService tenantProvisioningService;
    private final TenantService tenantService;

    @PostMapping("/auth/login")
    @Operation(summary = "Platform admin login")
    public ResponseEntity<PlatformLoginResponse> platformLogin(@Valid @RequestBody PlatformLoginRequest request) {
        return ResponseEntity.ok(platformAuthService.login(request));
    }

    @PostMapping("/tenants")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(summary = "Provision a new tenant database")
    public ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        TenantResponse response = tenantProvisioningService.provisionTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tenants")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @Operation(summary = "List all tenants")
    public ResponseEntity<List<TenantResponse>> listTenants() {
        return ResponseEntity.ok(tenantService.listTenantResponses());
    }
}
