package com.avnzor.oms_backend.tenants.config;

import com.avnzor.oms_backend.tenants.datasource.DataSourceManager;
import com.avnzor.oms_backend.tenants.datasource.TenantRoutingDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.avnzor.oms_backend.auth.repository",
                "com.avnzor.oms_backend.audit.repository"
        },
        entityManagerFactoryRef = "tenantEntityManagerFactory",
        transactionManagerRef = "tenantTransactionManager"
)
public class TenantJpaConfig {

    @Bean
    @Primary
    @DependsOn("tenantRegistry")
    DataSource tenantRoutingDataSource(DataSourceManager dataSourceManager) {
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();
        var targets = new HashMap<>(dataSourceManager.getTargetDataSources());
        routingDataSource.setTargetDataSources(targets);

        if (!targets.isEmpty()) {
            routingDataSource.setDefaultTargetDataSource(targets.values().iterator().next());
            routingDataSource.setLenientFallback(true);
        }

        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    @Bean
    @Primary
    LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("tenantRoutingDataSource") DataSource tenantRoutingDataSource
    ) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

        return builder
                .dataSource(tenantRoutingDataSource)
                .packages(
                        "com.avnzor.oms_backend.auth.entity",
                        "com.avnzor.oms_backend.audit.entity",
                        "com.avnzor.oms_backend.purchases.entity",
                        "com.avnzor.oms_backend.sales.entity",
                        "com.avnzor.oms_backend.warehouse.entity"
                )
                .persistenceUnit("tenant")
                .properties(properties)
                .build();
    }

    @Bean
    @Primary
    PlatformTransactionManager tenantTransactionManager(
            @Qualifier("tenantEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
