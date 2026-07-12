-- Normalize details column for MySQL 5.5 (LONGTEXT) and MySQL 8+ compatibility.
-- JSON type is not available on older MySQL versions used in some environments.

ALTER TABLE audit_log
    MODIFY COLUMN details LONGTEXT DEFAULT NULL
    COMMENT 'Structured before/after payload or request metadata';
