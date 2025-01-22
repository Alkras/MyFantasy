CREATE SEQUENCE characters_seq START WITH 1 increment by 50;

CREATE TABLE IF NOT EXISTS characters
(
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    level INT          NOT NULL,
    type  VARCHAR(255),
    x     BIGINT,
    y     BIGINT,
    money DECIMAL(6, 2)
);

CREATE TABLE IF NOT EXISTS locations
(
    x                     BIGINT,
    y                     BIGINT,
    location_type         VARCHAR(255) NOT NULL,
    location_biome        VARCHAR(255) NOT NULL,
    location_threat_level INT,
    PRIMARY KEY (x, y)
);

CREATE TABLE IF NOT EXISTS item_templates
(
    name      VARCHAR(255) PRIMARY KEY,
    item_type VARCHAR(255),
    price     DECIMAL(6, 2),
    strength  INT
);

CREATE SEQUENCE items_seq START WITH 1 increment by 50;

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    character_id BIGINT,
    name         VARCHAR(255),
    item_type    VARCHAR(255),
    price        DECIMAL(6, 2),
    strength     INT
);

