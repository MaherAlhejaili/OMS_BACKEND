package com.avnzor.oms_backend.tenants.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "tenants")
@Getter
@Setter
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TenantStatus status = TenantStatus.PROVISIONING;

    @Column(name = "database_host", nullable = false)
    private String databaseHost;

    @Column(name = "database_port", nullable = false)
    private Integer databasePort = 3306;

    @Column(name = "database_name", nullable = false, length = 128)
    private String databaseName;

    @Column(name = "database_username", nullable = false, length = 128)
    private String databaseUsername;

    @Column(name = "database_password_encrypted", nullable = false, length = 512)
    private String databasePasswordEncrypted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
