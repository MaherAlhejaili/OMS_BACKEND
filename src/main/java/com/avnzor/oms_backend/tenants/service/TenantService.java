package com.avnzor.oms_backend.tenants.service;

import com.avnzor.oms_backend.tenants.context.TenantContext;
import com.avnzor.oms_backend.tenants.entity.Tenant;
import com.avnzor.oms_backend.tenants.entity.TenantStatus;
import com.avnzor.oms_backend.tenants.exception.TenantDisabledException;
import com.avnzor.oms_backend.tenants.exception.TenantNotFoundException;
import com.avnzor.oms_backend.tenants.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    @Transactional(value = "platformTransactionManager", readOnly = true)
    public Tenant resolveActiveTenant(String tenantSlug) {
        Tenant tenant = tenantRepository.findBySlug(tenantSlug)
                .orElseThrow(() -> new TenantNotFoundException(tenantSlug));

        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new TenantDisabledException(tenantSlug);
        }

        log.debug("Resolved active tenant slug={}", tenantSlug);
        return tenant;
    }

    @Transactional(value = "platformTransactionManager", readOnly = true)
    public Tenant getById(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantNotFoundException(String.valueOf(tenantId)));
    }

    @Transactional(value = "platformTransactionManager", readOnly = true)
    public List<Tenant> listTenants() {
        return tenantRepository.findAll();
    }

    public TenantContext toContext(Tenant tenant) {
        return new TenantContext(tenant.getId(), tenant.getSlug());
    }
}
