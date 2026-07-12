-- Address, sales jobs, and supplier inventory legacy tables.

CREATE TABLE IF NOT EXISTS sma_addresses
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    company_id      INT          NOT NULL,
    line1           VARCHAR(255) NULL,
    line2           VARCHAR(255) NULL,
    city            VARCHAR(255) NULL,
    postal_code     VARCHAR(255) NULL,
    state           VARCHAR(255) NULL,
    country         VARCHAR(255) NULL,
    phone           VARCHAR(255) NULL,
    latitude        VARCHAR(255) NULL,
    longitude       VARCHAR(255) NULL,
    first_name      VARCHAR(255) NULL,
    last_name       VARCHAR(255) NULL,
    mobile_verified INT          NULL,
    is_default      TINYINT(1)   DEFAULT 0,
    CONSTRAINT fk_addresses_company
        FOREIGN KEY (company_id) REFERENCES sma_companies (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sma_sales_jobs
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    reference_no INT          NOT NULL,
    assigned_to  INT          NULL,
    assigned_by  INT          NULL,
    updated_by   INT          NULL,
    return_id    VARCHAR(100) NULL,
    created_at   TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sma_supplier_inventory
(
    id                    INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id           INT            NULL,
    supplier_inventory_id INT            NULL,
    source_id             INT            NULL,
    item_id               INT            NULL,
    product_code          VARCHAR(255)   NULL,
    quantity              DECIMAL(25, 2) NULL,
    location              VARCHAR(255)   NULL,
    last_synced_at        DATETIME       NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
