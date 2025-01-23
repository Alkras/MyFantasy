CREATE SEQUENCE monsters_seq START WITH 1 increment by 50;

CREATE TABLE IF NOT EXISTS monsters
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    hit_points INT          NOT NULL,
    armor      INT          NOT NULL,
    agility    INT          NOT NULL,
    attack     INT          NOT NULL,
    x          BIGINT,
    y          BIGINT
);