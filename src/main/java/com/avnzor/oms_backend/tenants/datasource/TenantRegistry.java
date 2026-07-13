package com.avnzor.oms_backend.tenants.datasource;

import com.avnzor.oms_backend.tenants.encryption.CredentialEncryptor;
import com.avnzor.oms_backend.tenants.entity.Tenant;
import com.avnzor.oms_backend.tenants.entity.TenantStatus;
import com.avnzor.oms_backend.tenants.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TenantRegistry {

    private final TenantRepository tenantRepository;
    private final DataSourceManager dataSourceManager;
    private final CredentialEncryptor credentialEncryptor;

    public void initializeActiveTenants() {
        List<Tenant> activeTenants = tenantRepository.findByStatus(TenantStatus.ACTIVE);
        log.info("Initializing {} active tenant datasource(s)", activeTenants.size());

        for (Tenant tenant : activeTenants) {
            registerTenant(tenant);
        }
    }

    public void registerTenant(Tenant tenant) {
        dataSourceManager.getOrCreate(tenant);
        log.info("Tenant registered slug={} database={}", tenant.getSlug(), tenant.getDatabaseName());
    }

    public Tenant createTenantRecord(
            String slug,
            String name,
            String databaseHost,
            int databasePort,
            String databaseName,
            String databaseUsername,
            String databasePassword
    ) {
        Tenant tenant = new Tenant();
        tenant.setSlug(slug);
        tenant.setName(name);
        tenant.setStatus(TenantStatus.PROVISIONING);
        tenant.setDatabaseHost(databaseHost);
        tenant.setDatabasePort(databasePort);
        tenant.setDatabaseName(databaseName);
        tenant.setDatabaseUsername(databaseUsername);
        tenant.setDatabasePasswordEncrypted(credentialEncryptor.encrypt(databasePassword));
        return tenantRepository.save(tenant);
    }

    public void markActive(Tenant tenant) {
        tenant.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(tenant);
        registerTenant(tenant);
    }
}
