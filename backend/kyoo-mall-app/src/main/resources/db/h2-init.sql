-- H2 内存库初始化（PostgreSQL 兼容模式），与 backend/db/init.sql 保持同一结构
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(32)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    nickname    VARCHAR(64),
    email       VARCHAR(128),
    status      SMALLINT     NOT NULL DEFAULT 1,
    create_time TIMESTAMP    NOT NULL DEFAULT now(),
    update_time TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS product (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(128)  NOT NULL,
    description TEXT,
    cover_image VARCHAR(512),
    price       NUMERIC(12, 2) NOT NULL DEFAULT 0,
    stock       INT            NOT NULL DEFAULT 0,
    status      SMALLINT       NOT NULL DEFAULT 1,
    create_time TIMESTAMP      NOT NULL DEFAULT now(),
    update_time TIMESTAMP      NOT NULL DEFAULT now()
);

-- 演示账号 admin / 123456（BCrypt）
INSERT INTO sys_user (username, password, nickname, status)
VALUES ('admin', '$2a$10$xdVkmXn8EpO0SOs99qujIeSIdJyj2oru5dcWKbuPVJU8sMIhVAK/q', '管理员', 1);

INSERT INTO product (name, description, price, stock, status) VALUES
    ('示例商品 A', '这是一件示例商品，可在后台修改或删除。', 99.00, 100, 1),
    ('示例商品 B', '这是一件示例商品，可在后台修改或删除。', 199.00, 50, 1);
