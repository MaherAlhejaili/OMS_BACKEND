package com.avnzor.oms_backend.tenants.service;

import com.avnzor.oms_backend.common.exception.BadRequestException;
import com.avnzor.oms_backend.tenants.datasource.DataSourceManager;
import com.avnzor.oms_backend.tenants.datasource.TenantRegistry;
import com.avnzor.oms_backend.tenants.datasource.TenantRoutingDataSource;
import com.avnzor.oms_backend.tenants.dto.CreateTenantRequest;
import com.avnzor.oms_backend.tenants.dto.TenantResponse;
import com.avnzor.oms_backend.tenants.entity.Tenant;
import com.avnzor.oms_backend.tenants.mapper.TenantMapper;
import com.avnzor.oms_backend.tenants.repository.TenantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;

@Slf4j
@Service
public class TenantProvisioningService {

    private final TenantRegistry tenantRegistry;
    private final TenantMapper tenantMapper;
    private final TenantRepository tenantRepository;
    private final DataSourceManager dataSourceManager;
    private final DataSource platformDataSource;
    private final DataSource tenantRoutingDataSource;

    public TenantProvisioningService(
            TenantRegistry tenantRegistry,
            TenantMapper tenantMapper,
            TenantRepository tenantRepository,
            DataSourceManager dataSourceManager,
            @Qualifier("platformDataSource") DataSource platformDataSource,
            @Qualifier("tenantRoutingDataSource") DataSource tenantRoutingDataSource
    ) {
        this.tenantRegistry = tenantRegistry;
        this.tenantMapper = tenantMapper;
        this.tenantRepository = tenantRepository;
        this.dataSourceManager = dataSourceManager;
        this.platformDataSource = platformDataSource;
        this.tenantRoutingDataSource = tenantRoutingDataSource;
    }

    @Transactional("platformTransactionManager")
    public TenantResponse provisionTenant(CreateTenantRequest request) {
        if (tenantRepository.existsBySlug(request.slug())) {
            throw new BadRequestException("Tenant slug already exists: " + request.slug());
        }

        log.info("Provisioning tenant slug={} database={}", request.slug(), request.databaseName());

        createDatabaseIfNotExists(request.databaseName());

        Tenant tenant = tenantRegistry.createTenantRecord(
                request.slug(),
                request.name(),
                request.databaseHost(),
                request.databasePort(),
                request.databaseName(),
                request.databaseUsername(),
                request.databasePassword()
        );

        tenantRegistry.markActive(tenant);
        refreshRoutingDataSource();

        log.info("Tenant provisioned successfully slug={}", tenant.getSlug());
        return tenantMapper.toResponse(tenant);
    }

    private void createDatabaseIfNotExists(String databaseName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(platformDataSource);
        jdbcTemplate.execute(
                "CREATE DATABASE IF NOT EXISTS `" + databaseName + "` "
                        + "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
        );
        log.info("Ensured tenant database exists database={}", databaseName);
    }

    private void refreshRoutingDataSource() {
        if (tenantRoutingDataSource instanceof TenantRoutingDataSource routingDataSource) {
            var targets = new HashMap<>(dataSourceManager.getTargetDataSources());
            routingDataSource.setTargetDataSources(targets);
            if (!targets.isEmpty()) {
                routingDataSource.setDefaultTargetDataSource(targets.values().iterator().next());
                routingDataSource.setLenientFallback(true);
            }
            routingDataSource.afterPropertiesSet();
        }
    }
}
