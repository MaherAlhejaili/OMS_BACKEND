package com.avnzor.oms_backend.support;

import org.springframework.test.context.DynamicPropertyRegistry;

public final class TestPropertyRegistrySupport {

    private TestPropertyRegistrySupport() {
    }

    public static void registerPlatformDataSource(
            DynamicPropertyRegistry registry,
            String jdbcUrl,
            String username,
            String password,
            String host,
            int port
    ) {
        registry.add("app.datasource.platform.url", () -> jdbcUrl);
        registry.add("app.datasource.platform.username", () -> username);
        registry.add("app.datasource.platform.password", () -> password);
        registry.add("app.datasource.platform.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("test.mysql.host", () -> host);
        registry.add("test.mysql.port", () -> String.valueOf(port));
    }
}
