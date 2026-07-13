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

import java.time.LocalDateTime;

@Entity
@Table(name = "tenants")
@Getter
@Setter
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", nullable = false, length = 64, unique = true)
    private String slug;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private TenantStatus status;

    @Column(name = "database_host", nullable = false, length = 255)
    private String databaseHost;

    @Column(name = "database_port", nullable = false)
    private Integer databasePort;

    @Column(name = "database_name", nullable = false, length = 128)
    private String databaseName;

    @Column(name = "database_username", nullable = false, length = 128)
    private String databaseUsername;

    @Column(name = "database_password_encrypted", nullable = false, length = 512)
    private String databasePasswordEncrypted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
