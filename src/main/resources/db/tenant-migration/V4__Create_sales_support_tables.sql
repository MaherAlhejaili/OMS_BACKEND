-- Supporting legacy tables for sales enrichment (products, customers, shopify, shipments).

CREATE TABLE IF NOT EXISTS sma_products
(
    id                INT AUTO_INCREMENT PRIMARY KEY,
    code              VARCHAR(255)   NOT NULL,
    name              VARCHAR(255)   NOT NULL,
    name_ar           VARCHAR(255)   NULL,
    cost              DECIMAL(6, 2)  NULL,
    price             DECIMAL(6, 2)  NULL,
    image             VARCHAR(255)   NULL,
    quantity          INT            NULL,
    promotion         INT            NULL,
    start_date        DATE           NULL,
    end_date          DATE           NULL,
    promo_price       DECIMAL(6, 2)  NULL,
    product_details   TEXT           NULL,
    product_details_ar TEXT          NULL,
    details           TEXT           NULL,
    details_ar        TEXT           NULL,
    type              VARCHAR(255)   NULL,
    bundle_items_ids  TEXT           NULL,
    slug              VARCHAR(255)   NULL,
    brand             INT            NULL,
    featured          INT            NULL,
    special_offer     INT            NULL,
    best_seller       INT            NULL,
    sequence_code     VARCHAR(255)   NULL,
    category_id       INT            NULL,
    speciality_id     INT            NULL,
    topic_id          INT            NULL,
    tax_rate          INT            NULL,
    subcategory_id    INT            NULL,
    tags              TEXT           NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sma_companies
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(255) NULL,
    company         VARCHAR(255) NULL,
    email           VARCHAR(255) NULL,
    phone           VARCHAR(255) NULL,
    group_id        INT          NOT NULL,
    group_name      VARCHAR(255) NULL,
    address         VARCHAR(255) NULL,
    city            VARCHAR(255) NULL,
    state           VARCHAR(255) NULL,
    country         VARCHAR(255) NULL,
    sequence_code   VARCHAR(255) NULL,
    line2           VARCHAR(255) NULL,
    latitude        VARCHAR(255) NULL,
    longitude       VARCHAR(255) NULL,
    postal_code     VARCHAR(255) NULL,
    first_name      VARCHAR(255) NULL,
    last_name       VARCHAR(255) NULL,
    mobile_verified INT          NULL,
    profile_pic     VARCHAR(255) NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sma_shopify_orders
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    shopify_order_id BIGINT       NOT NULL,
    order_number     VARCHAR(50)  NOT NULL,
    payload          LONGTEXT     NOT NULL,
    wms_status       VARCHAR(20)  DEFAULT 'PENDING',
    created_at       TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sma_shipment_creations
(
    id                INT AUTO_INCREMENT PRIMARY KEY,
    order_id          INT          NOT NULL,
    provider          VARCHAR(255) NOT NULL,
    number_of_pieces  INT          NOT NULL,
    tracking_number   VARCHAR(255) NULL,
    label_url         TEXT         NULL,
    status            VARCHAR(255) NULL,
    raw_request       LONGTEXT     NULL,
    raw_response      LONGTEXT     NULL,
    created_at        TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sma_shipment_items
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    order_id     INT          NOT NULL,
    item_id      INT          NULL,
    product_code VARCHAR(255) NULL,
    qty          DECIMAL(12, 2) NULL,
    tracking_id  VARCHAR(255) NULL,
    carrier      VARCHAR(255) NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
