package com.avnzor.oms_backend.tenants.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.multi-tenancy")
public record MultiTenancyProperties(
        String encryptionKey,
        int datasourceMaximumPoolSize,
        int datasourceMinimumIdle
) {
    public MultiTenancyProperties {
        if (datasourceMaximumPoolSize <= 0) {
            datasourceMaximumPoolSize = 5;
        }
        if (datasourceMinimumIdle < 0) {
            datasourceMinimumIdle = 1;
        }
    }
}
