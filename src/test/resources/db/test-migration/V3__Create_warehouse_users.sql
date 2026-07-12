-- Test-only schema support. Production already contains sma_warehouse_users in the baseline schema.
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

INSERT INTO sma_warehouse_users (username, password, name, role, department)
VALUES ('logistic.user', 'password', 'Logistic User', 'worker', 'Logistic'),
       ('warehouse.user', 'password', 'Warehouse User', 'worker', 'Warehouse');
