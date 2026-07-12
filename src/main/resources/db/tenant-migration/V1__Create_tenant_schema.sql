-- Tenant database schema: isolated business data per tenant (no tenant_id columns).

CREATE TABLE sma_warehouse_users
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(255) NULL,
    password   VARCHAR(255) NULL,
    name       VARCHAR(255) NULL,
    role       VARCHAR(255) NULL,
    department VARCHAR(255) NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE audit_log
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    event_type  VARCHAR(100) NOT NULL COMMENT 'Action performed, e.g. API_REQUEST',
    entity_type VARCHAR(100) NOT NULL COMMENT 'Domain entity affected, e.g. orders',
    entity_id   VARCHAR(100)          DEFAULT NULL,
    actor       VARCHAR(255)          DEFAULT NULL,
    details     LONGTEXT              DEFAULT NULL COMMENT 'JSON metadata',
    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_audit_log_created_at (created_at),
    KEY idx_audit_log_entity (entity_type, entity_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
