package com.avnzor.oms_backend.support;

import com.avnzor.oms_backend.OmsBackendApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(classes = OmsBackendApplication.class)
@Import(TestMultiTenancyConfig.class)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    private static final DockerImageName MYSQL_IMAGE = DockerImageName.parse("mysql:8.4");

    @Container
    @SuppressWarnings("resource")
    protected static final MySQLContainer<?> MYSQL = new MySQLContainer<>(MYSQL_IMAGE)
            .withDatabaseName("oms_test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("testcontainers/init-tenant-database.sql");

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        TestPropertyRegistrySupport.registerPlatformDataSource(
                registry,
                MYSQL.getJdbcUrl(),
                MYSQL.getUsername(),
                MYSQL.getPassword(),
                MYSQL.getHost(),
                MYSQL.getMappedPort(3306)
        );
    }
}
