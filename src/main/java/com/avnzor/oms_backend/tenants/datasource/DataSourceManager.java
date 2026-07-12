package com.avnzor.oms_backend.tenants.datasource;

import com.avnzor.oms_backend.tenants.config.MultiTenancyProperties;
import com.avnzor.oms_backend.tenants.encryption.CredentialEncryptor;
import com.avnzor.oms_backend.tenants.entity.Tenant;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSourceManager {

    private final CredentialEncryptor credentialEncryptor;
    private final MultiTenancyProperties multiTenancyProperties;

    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    public DataSource getOrCreate(Tenant tenant) {
        return dataSources.computeIfAbsent(tenant.getSlug(), slug -> createDataSource(tenant));
    }

    public void register(Tenant tenant) {
        DataSource existing = dataSources.put(tenant.getSlug(), createDataSource(tenant));
        closeQuietly(existing);
        log.info("Registered datasource for tenant slug={}", tenant.getSlug());
    }

    public void unregister(String tenantSlug) {
        DataSource removed = dataSources.remove(tenantSlug);
        closeQuietly(removed);
        log.info("Unregistered datasource for tenant slug={}", tenantSlug);
    }

    public Map<Object, Object> getTargetDataSources() {
        return Map.copyOf(dataSources);
    }

    public boolean isRegistered(String tenantSlug) {
        return dataSources.containsKey(tenantSlug);
    }

    private DataSource createDataSource(Tenant tenant) {
        String password = credentialEncryptor.decrypt(tenant.getDatabasePasswordEncrypted());

        HikariConfig config = new HikariConfig();
        config.setPoolName("tenant-" + tenant.getSlug());
        config.setJdbcUrl(buildJdbcUrl(tenant));
        config.setUsername(tenant.getDatabaseUsername());
        config.setPassword(password);
        config.setMaximumPoolSize(multiTenancyProperties.datasourceMaximumPoolSize());
        config.setMinimumIdle(multiTenancyProperties.datasourceMinimumIdle());
        config.setConnectionTimeout(30_000);
        config.setIdleTimeout(600_000);
        config.setMaxLifetime(1_800_000);

        log.info("Created datasource pool for tenant slug={} database={}", tenant.getSlug(), tenant.getDatabaseName());
        return new HikariDataSource(config);
    }

    private String buildJdbcUrl(Tenant tenant) {
        return "jdbc:mysql://" + tenant.getDatabaseHost() + ":" + tenant.getDatabasePort()
                + "/" + tenant.getDatabaseName()
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private void closeQuietly(DataSource dataSource) {
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.close();
        }
    }
}
