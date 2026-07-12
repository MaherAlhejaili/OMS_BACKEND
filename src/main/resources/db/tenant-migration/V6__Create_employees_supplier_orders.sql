CREATE TABLE IF NOT EXISTS sma_employees
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sma_supplier_orders
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id      INT          NULL,
    order_id         INT          NOT NULL,
    request_payload  TEXT         NULL,
    response_payload TEXT         NULL,
    status           VARCHAR(255) NULL,
    updated_at       DATETIME     NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
