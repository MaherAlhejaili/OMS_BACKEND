CREATE TABLE IF NOT EXISTS sma_sale_status_history
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    sale_id     INT          NULL,
    old_status  VARCHAR(50)  NULL,
    new_status  VARCHAR(50)  NOT NULL,
    changed_by  INT          NULL,
    note        VARCHAR(255) NULL,
    changed_at  DATETIME     NULL,
    CONSTRAINT fk_sale_status_history_sale
        FOREIGN KEY (sale_id) REFERENCES sma_sales (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
