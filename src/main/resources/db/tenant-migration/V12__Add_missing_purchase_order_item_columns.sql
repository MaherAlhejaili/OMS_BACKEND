ALTER TABLE sma_purchase_order_items
    ADD COLUMN original_quantity DECIMAL(12, 2) NULL DEFAULT 0,
    ADD COLUMN old_scanned_quantity DECIMAL(12, 2) NULL DEFAULT 0,
    ADD COLUMN scanned_time DATETIME NULL,
    ADD COLUMN shelf_life VARCHAR(255) NULL,
    ADD COLUMN net_unit_cost_old VARCHAR(255) NULL;
