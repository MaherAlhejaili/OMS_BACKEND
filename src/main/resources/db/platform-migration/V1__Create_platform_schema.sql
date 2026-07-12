-- Platform database schema: tenant registry and platform administrators only.

CREATE TABLE tenants
(
    id                         BIGINT       NOT NULL AUTO_INCREMENT,
    slug                       VARCHAR(64)  NOT NULL COMMENT 'Unique tenant identifier used in login and JWT',
    name                       VARCHAR(255) NOT NULL,
    status                     VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, DISABLED, PROVISIONING',
    database_host              VARCHAR(255) NOT NULL,
    database_port              INT          NOT NULL DEFAULT 3306,
    database_name              VARCHAR(128) NOT NULL,
    database_username          VARCHAR(128) NOT NULL,
    database_password_encrypted VARCHAR(512) NOT NULL COMMENT 'AES-GCM encrypted database password',
    created_at                 TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at                 TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_tenants_slug (slug),
    KEY idx_tenants_status (status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE platform_users
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    username   VARCHAR(150) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    role       VARCHAR(64)  NOT NULL DEFAULT 'PLATFORM_ADMIN',
    active     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_platform_users_username (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
