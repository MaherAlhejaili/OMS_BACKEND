package com.avnzor.oms_backend.tenants.config;

import com.avnzor.oms_backend.tenants.bootstrap.DevEnvironmentInitializer;
import com.avnzor.oms_backend.tenants.datasource.DataSourceManager;
import com.avnzor.oms_backend.tenants.datasource.TenantRegistry;
import com.avnzor.oms_backend.tenants.encryption.CredentialEncryptor;
import com.avnzor.oms_backend.tenants.entity.PlatformUser;
import com.avnzor.oms_backend.tenants.entity.Tenant;
import com.avnzor.oms_backend.tenants.flyway.TenantFlywayService;
import com.avnzor.oms_backend.tenants.repository.PlatformUserRepository;
import com.avnzor.oms_backend.tenants.repository.TenantRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.avnzor.oms_backend.tenants.repository",
        entityManagerFactoryRef = "platformEntityManagerFactory",
        transactionManagerRef = "platformTransactionManager"
)
public class PlatformJpaConfig {

    @Bean
    DataSource platformDataSource(
            @Value("${app.datasource.platform.url}") String url,
            @Value("${app.datasource.platform.username}") String username,
            @Value("${app.datasource.platform.password}") String password,
            @Value("${app.datasource.platform.driver-class-name}") String driverClassName
    ) {
        HikariConfig config = new HikariConfig();
        config.setPoolName("platform");
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        config.setMaximumPoolSize(5);
        return new HikariDataSource(config);
    }

    @Bean
    @org.springframework.context.annotation.DependsOn("platformFlywayInitializer")
    LocalContainerEntityManagerFactoryBean platformEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("platformDataSource") DataSource platformDataSource
    ) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

        return builder
                .dataSource(platformDataSource)
                .packages(Tenant.class, PlatformUser.class)
                .persistenceUnit("platform")
                .properties(properties)
                .build();
    }

    @Bean
    PlatformTransactionManager platformTransactionManager(
            @Qualifier("platformEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    @org.springframework.context.annotation.DependsOn("platformEntityManagerFactory")
    TenantRegistry tenantRegistry(
            TenantRepository tenantRepository,
            PlatformUserRepository platformUserRepository,
            DataSourceManager dataSourceManager,
            TenantFlywayService tenantFlywayService,
            CredentialEncryptor credentialEncryptor,
            PasswordEncoder passwordEncoder,
            Environment environment,
            @Qualifier("platformDataSource") DataSource platformDataSource
    ) {
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            DevEnvironmentInitializer devInitializer = new DevEnvironmentInitializer(
                    tenantRepository,
                    platformUserRepository,
                    credentialEncryptor,
                    passwordEncoder,
                    platformDataSource
            );
            devInitializer.initializeIfNeeded();
        }

        TenantRegistry registry = new TenantRegistry(
                tenantRepository,
                dataSourceManager,
                tenantFlywayService,
                credentialEncryptor
        );
        registry.initializeActiveTenants();
        return registry;
    }
}
