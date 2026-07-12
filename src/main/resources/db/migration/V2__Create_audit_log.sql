-- Adds application-owned audit logging. Does not modify any existing production tables.
-- Version 2 is intentional: baseline-version 1 represents the pre-Flyway schema.

CREATE TABLE audit_log
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    event_type  VARCHAR(100) NOT NULL COMMENT 'Action performed, e.g. ORDER_CREATED',
    entity_type VARCHAR(100) NOT NULL COMMENT 'Domain entity affected, e.g. orders',
    entity_id   VARCHAR(100)          DEFAULT NULL COMMENT 'Primary key or business identifier of the entity',
    actor       VARCHAR(255)          DEFAULT NULL COMMENT 'User or system principal that performed the action',
    tenant_id   VARCHAR(64)           DEFAULT NULL COMMENT 'Reserved for future multi-tenant support',
    details     JSON                  DEFAULT NULL COMMENT 'Structured before/after payload or request metadata',
    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_audit_log_created_at (created_at),
    KEY idx_audit_log_entity (entity_type, entity_id),
    KEY idx_audit_log_tenant (tenant_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
