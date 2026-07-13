package com.avnzor.oms_backend.support;

import com.avnzor.oms_backend.auth.entity.WarehouseWorker;
import com.avnzor.oms_backend.auth.repository.WarehouseWorkerRepository;
import com.avnzor.oms_backend.tenants.bootstrap.TenantBootstrapContext;
import com.avnzor.oms_backend.tenants.bootstrap.TenantBootstrapContributor;
import com.avnzor.oms_backend.tenants.context.TenantContextHolder;
import com.avnzor.oms_backend.tenants.entity.PlatformUser;
import com.avnzor.oms_backend.tenants.entity.Tenant;
import com.avnzor.oms_backend.tenants.entity.TenantStatus;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

@TestConfiguration
@Profile("test")
public class TestMultiTenancyConfig {

    @Bean
    @Order(0)
    TenantBootstrapContributor testTenantBootstrapContributor(Environment environment) {
        return context -> seedPlatformTenant(context, environment);
    }

    @Bean
    ApplicationRunner testTenantUserSeeder(WarehouseWorkerRepository warehouseWorkerRepository) {
        return args -> seedTenantUsers(warehouseWorkerRepository);
    }

    private void seedPlatformTenant(TenantBootstrapContext context, Environment environment) {
        seedPlatformAdmin(context);

        String slug = environment.getProperty("test.tenant.slug", "test-tenant");
        if (context.tenantRepository().existsBySlug(slug)) {
            return;
        }

        String tenantDatabase = environment.getProperty("test.tenant.database-name", "tenant_test");
        String host = environment.getRequiredProperty("test.mysql.host");
        int port = Integer.parseInt(environment.getRequiredProperty("test.mysql.port"));
        String username = context.environment().getProperty("app.datasource.platform.username", "test");
        String password = context.environment().getProperty("app.datasource.platform.password", "test");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(context.platformDataSource());
        jdbcTemplate.execute(
                "CREATE DATABASE IF NOT EXISTS `" + tenantDatabase + "` "
                        + "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
        );

        Tenant tenant = new Tenant();
        tenant.setSlug(slug);
        tenant.setName("Test Tenant");
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setDatabaseHost(host);
        tenant.setDatabasePort(port);
        tenant.setDatabaseName(tenantDatabase);
        tenant.setDatabaseUsername(username);
        tenant.setDatabasePasswordEncrypted(context.credentialEncryptor().encrypt(password));
        context.tenantRepository().save(tenant);
    }

    private void seedPlatformAdmin(TenantBootstrapContext context) {
        if (context.platformUserRepository().findByUsername("platform.admin").isPresent()) {
            return;
        }

        PlatformUser admin = new PlatformUser();
        admin.setUsername("platform.admin");
        admin.setName("Platform Administrator");
        admin.setRole("PLATFORM_ADMIN");
        admin.setPassword(context.passwordEncoder().encode("admin123"));
        admin.setActive(true);
        context.platformUserRepository().save(admin);
    }

    private void seedTenantUsers(WarehouseWorkerRepository warehouseWorkerRepository) {
        try {
            TenantContextHolder.set(TestUserFactory.TEST_TENANT_ID, TestUserFactory.TEST_TENANT_SLUG);

            if (warehouseWorkerRepository.findByUsername("logistic.user").isEmpty()) {
                WarehouseWorker worker = TestUserFactory.logisticWorker();
                warehouseWorkerRepository.save(worker);
            }
            if (warehouseWorkerRepository.findByUsername("warehouse.user").isEmpty()) {
                warehouseWorkerRepository.save(TestUserFactory.warehouseWorker());
            }
        } finally {
            TenantContextHolder.clear();
        }
    }
}
