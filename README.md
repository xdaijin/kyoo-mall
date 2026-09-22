# Kyoo Mall

前后端分离的商城系统。

- **后端**：Spring Boot 3 + Java 21 + Maven 多模块（DDD modulith）+ MyBatis-Plus + PostgreSQL + Spring Security (JWT)
- **前端**：Vue 3 + Vite + Element Plus + Pinia + Vue Router + axios

## 目录结构

```
kyoo-mall/
├── backend/                        # Maven 多模块父工程（端口 8080，context-path /api）
│   ├── db/init.sql                 # PostgreSQL 初始化脚本（建表 + 示例数据）
│   ├── kyoo-mall-common/           # 共享内核：Result、BusinessException（零 Spring 依赖）
│   ├── kyoo-mall-user/             # 用户/身份限界上下文：注册、登录、JWT 签发
│   ├── kyoo-mall-product/          # 商品限界上下文
│   └── kyoo-mall-app/              # 组装层/启动模块：启动类、Security 配置、运行配置
├── frontend/                       # Vite 前端（端口 5173，/api 代理到 8080）
└── docker-compose.yml              # PostgreSQL 一键启动
```

每个业务模块内部按 DDD 四层分包（以 user 为例）：

```
com.kyoo.mall.user/
├── interfaces/       # Controller + DTO
├── application/      # 应用服务（用例编排，事务边界）
├── domain/           # model（领域对象）+ repository（仓储接口）
└── infrastructure/   # persistence（Mapper + 仓储实现）等技术细节
```

**依赖规则**：`app → 业务模块 → common`，业务模块之间禁止互相依赖。
这样设计是为了将来能按限界上下文直接拆分为微服务：每个业务模块配上自己的启动模块即可独立成服务。

> 📐 **完整的目录架构规范（强制）见 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)**，
> 核心规则由 ArchUnit 测试自动校验（`mvn test` 违反即失败）。

## 快速开始

### 1. 启动数据库

```bash
docker compose up -d
```

首次启动会自动执行 `backend/db/init.sql` 建表并插入示例数据（含演示账号 `admin / 123456`）。

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run -pl kyoo-mall-app -am
```

数据库连接、JWT 密钥等均可通过环境变量覆盖，见 `backend/kyoo-mall-app/src/main/resources/application.yml`。

没有 Docker/PostgreSQL 时可用 H2 内存库（数据不持久化，重启重置）：

```bash
mvn spring-boot:run -pl kyoo-mall-app -am -Dspring-boot.run.profiles=h2
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173 ，使用 `admin / 123456` 登录（或注册新账号）。

## API 约定

- 统一响应结构：`{ "code": 0, "message": "ok", "data": ... }`，`code = 0` 表示成功
- 认证方式：`Authorization: Bearer <token>`，`POST /api/auth/login` 与 `POST /api/auth/register` 返回 token
- 商品读接口（`GET /api/products`）对游客开放，写接口需登录
