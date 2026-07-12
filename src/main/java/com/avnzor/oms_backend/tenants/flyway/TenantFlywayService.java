package com.avnzor.oms_backend.tenants.flyway;

import com.avnzor.oms_backend.tenants.entity.Tenant;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Slf4j
@Service
public class TenantFlywayService {

    private static final String TENANT_MIGRATION_LOCATION = "classpath:db/tenant-migration";

    public void migrateTenantDatabase(Tenant tenant, DataSource dataSource) {
        log.info("Running tenant Flyway migrations for slug={} database={}", tenant.getSlug(), tenant.getDatabaseName());

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(TENANT_MIGRATION_LOCATION)
                .baselineOnMigrate(false)
                .validateOnMigrate(true)
                .cleanDisabled(true)
                .load();

        flyway.migrate();
        log.info("Tenant Flyway migrations completed for slug={}", tenant.getSlug());
    }
}
