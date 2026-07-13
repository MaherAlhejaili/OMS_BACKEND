package com.avnzor.oms_backend.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(
        boolean allowLegacyPlaintextPassword
) {
}
