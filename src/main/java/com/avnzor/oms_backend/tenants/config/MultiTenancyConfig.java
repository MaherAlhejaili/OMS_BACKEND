package com.avnzor.oms_backend.tenants.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MultiTenancyProperties.class)
public class MultiTenancyConfig {
}
