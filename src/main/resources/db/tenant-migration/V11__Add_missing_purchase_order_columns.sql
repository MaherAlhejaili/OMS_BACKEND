ALTER TABLE sma_purchase_orders
    ADD COLUMN received_date DATE NULL,
    ADD COLUMN receiving_document VARCHAR(255) NULL,
    ADD COLUMN department_id INT NULL,
    ADD COLUMN marked_shelved TINYINT(1) NULL,
    ADD COLUMN flagged_supplier TINYINT(1) NULL;
