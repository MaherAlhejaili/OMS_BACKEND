package com.avnzor.oms_backend.tenants.bootstrap;

import com.avnzor.oms_backend.tenants.encryption.CredentialEncryptor;
import com.avnzor.oms_backend.tenants.entity.PlatformUser;
import com.avnzor.oms_backend.tenants.entity.Tenant;
import com.avnzor.oms_backend.tenants.entity.TenantStatus;
import com.avnzor.oms_backend.tenants.repository.PlatformUserRepository;
import com.avnzor.oms_backend.tenants.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
public class DevEnvironmentInitializer {

    private final TenantRepository tenantRepository;
    private final PlatformUserRepository platformUserRepository;
    private final CredentialEncryptor credentialEncryptor;
    private final PasswordEncoder passwordEncoder;
    private final DataSource platformDataSource;

    private String defaultTenantSlug = "tenant-1";
    private String defaultTenantDatabase = "tenant_1";
    private String defaultDatabaseHost = "127.0.0.1";
    private int defaultDatabasePort = 3306;
    private String defaultDatabaseUser = "root";
    private String defaultDatabasePassword = "";

    @Transactional("platformTransactionManager")
    public void initializeIfNeeded() {
        createPlatformAdminIfMissing();
        createDefaultTenantIfMissing();
    }

    private void createPlatformAdminIfMissing() {
        if (platformUserRepository.findByUsername("platform.admin").isPresent()) {
            return;
        }

        PlatformUser admin = new PlatformUser();
        admin.setUsername("platform.admin");
        admin.setName("Platform Administrator");
        admin.setRole("PLATFORM_ADMIN");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setActive(true);
        platformUserRepository.save(admin);
        log.info("Seeded dev platform admin username=platform.admin password=admin123");
    }

    private void createDefaultTenantIfMissing() {
        if (tenantRepository.existsBySlug(defaultTenantSlug)) {
            return;
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(platformDataSource);
        jdbcTemplate.execute(
                "CREATE DATABASE IF NOT EXISTS `" + defaultTenantDatabase + "` "
                        + "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
        );

        Tenant tenant = new Tenant();
        tenant.setSlug(defaultTenantSlug);
        tenant.setName("Tenant One");
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setDatabaseHost(defaultDatabaseHost);
        tenant.setDatabasePort(defaultDatabasePort);
        tenant.setDatabaseName(defaultTenantDatabase);
        tenant.setDatabaseUsername(defaultDatabaseUser);
        tenant.setDatabasePasswordEncrypted(credentialEncryptor.encrypt(defaultDatabasePassword));
        tenantRepository.save(tenant);

        log.info("Seeded dev tenant slug={} database={}", defaultTenantSlug, defaultTenantDatabase);
    }
}
