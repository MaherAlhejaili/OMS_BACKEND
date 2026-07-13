ALTER TABLE sma_purchase_orders
    ADD COLUMN shopify_synced TINYINT(1) NULL DEFAULT 0;
