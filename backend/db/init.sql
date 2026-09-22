-- Kyoo Mall 数据库初始化脚本
-- 通过 docker-compose 挂载到 /docker-entrypoint-initdb.d/ 自动执行，
-- 也可手动在 kyoo_mall 库中执行。

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

-- 示例数据（密码均为 BCrypt 加密的 "123456"）
INSERT INTO sys_user (username, password, nickname, status)
VALUES ('admin', '$2a$10$xdVkmXn8EpO0SOs99qujIeSIdJyj2oru5dcWKbuPVJU8sMIhVAK/q', '管理员', 1)
ON CONFLICT (username) DO NOTHING;

INSERT INTO product (name, description, price, stock, status) VALUES
    ('示例商品 A', '这是一件示例商品，可在后台修改或删除。', 99.00, 100, 1),
    ('示例商品 B', '这是一件示例商品，可在后台修改或删除。', 199.00, 50, 1)
ON CONFLICT DO NOTHING;
