package com.avnzor.oms_backend.tenants.flyway;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
public class PlatformFlywayInitializer {

    private static final String PLATFORM_MIGRATION_LOCATION = "classpath:db/platform-migration";

    public PlatformFlywayInitializer(@Qualifier("platformDataSource") DataSource platformDataSource) {
        log.info("Running platform Flyway migrations");

        Flyway flyway = Flyway.configure()
                .dataSource(platformDataSource)
                .locations(PLATFORM_MIGRATION_LOCATION)
                .baselineOnMigrate(false)
                .validateOnMigrate(true)
                .cleanDisabled(true)
                .load();

        flyway.migrate();
        log.info("Platform Flyway migrations completed");
    }
}
