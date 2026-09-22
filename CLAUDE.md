# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

Kyoo Mall 是一个前后端分离的商城系统，单仓库（monorepo）管理：

- `backend/` — Spring Boot 3.5 + Java 21 + **Maven 多模块（DDD modulith）**，MyBatis-Plus（基于 MyBatis）+ PostgreSQL，Spring Security + JWT 无状态认证；测试 JUnit 5 + Mockito + AssertJ（starter-test 自带）+ ArchUnit（架构守护）；类型转换 MapStruct；JSON 序列化 Jackson；日志 SLF4J + Log4j2（Logback 已在 app 层排除）；代码生成 Lombok。按限界上下文拆模块，目标是将来能直接拆成微服务。
- `frontend/` — Vue 3 + Vite + Element Plus + Pinia + Vue Router + axios

## 常用命令

```bash
# 数据库（首次启动自动执行 backend/db/init.sql 建表 + 示例数据，演示账号 admin/123456）
docker compose up -d

# 后端（backend/ 目录下）
mvn spring-boot:run -pl kyoo-mall-app -am   # 开发运行，端口 8080，context-path /api
mvn spring-boot:run -pl kyoo-mall-app -am -Dspring-boot.run.profiles=h2  # 无 PG 时用 H2 内存库
mvn package                                  # 全量打包（可执行 jar 在 kyoo-mall-app/target/）
mvn test                                     # 全部测试
mvn test -pl kyoo-mall-product -am -Dtest=类名   # 指定模块跑单个测试类（#方法名 可定位到方法）

# 前端（frontend/ 目录下）
npm run dev                  # 开发服务，端口 5173，/api 代理到 localhost:8080
npm run build                # 生产构建
```

## 后端架构（重点）

> **目录架构规范见 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)，为强制约定，新增代码必须遵从。**
> 核心依赖/分层规则由 `kyoo-mall-app` 的 `ArchitectureTest`（ArchUnit）自动校验，违反则 `mvn package` 失败。
> 新增限界上下文模块时，须同步补充架构测试规则并登记到 ARCHITECTURE.md。

### 模块划分与依赖规则

```
kyoo-mall-common   共享内核（Result、BusinessException、BaseEntity 审计字段基类），零 Spring 依赖
kyoo-mall-user     用户/身份上下文：注册、登录认证、JWT 签发
kyoo-mall-product  商品上下文
kyoo-mall-app      组装层/启动模块：唯一可执行模块，含启动类、Security 配置、全局异常处理、application.yml
```

- **依赖只允许 `app → 业务模块 → common`，业务模块之间禁止互相依赖。** 这是"将来可拆微服务"的核心约束：新增跨模块调用需求时，先确认是否应该通过应用服务编排或未来的 RPC/事件来做，而不是直接加 Maven 依赖。
- 新增限界上下文：新建 `kyoo-mall-<域>` 模块（参照 user/product 的 pom 与包结构），在父 pom `<modules>` 和 `kyoo-mall-app` 的依赖中注册。

### 模块内 DDD 四层

```
com.kyoo.mall.<域>/
├── interfaces/       Controller + DTO（只做参数校验与响应组装）
├── application/      应用服务（用例编排、事务边界），依赖 domain 的仓储接口
├── domain/           model（领域对象）+ repository（仓储接口，不感知 ORM）
└── infrastructure/   persistence（MyBatis-Plus Mapper + 仓储实现）等技术细节
```

约定与务实取舍：

- **领域模型与持久化对象合一**（model 直接带 MyBatis-Plus 注解），避免 DO/DTO 多层转换样板；表结构复杂化后再在 infrastructure 引入独立 PO。
- 仓储接口定义在 `domain/repository`，实现放在 `infrastructure/persistence`，应用层只依赖接口。
- 分页契约使用 common 的 `PageQuery`/`PageResult`（技术无关）；MyBatis-Plus 的 `Page`/`QueryWrapper` 只允许出现在 infrastructure（ArchUnit 校验）。
- 统一响应 `Result<T>`（`code=0` 成功）；业务代码抛 `BusinessException`，由 app 模块的 `GlobalExceptionHandler` 统一处理，不要在 Controller 手写 try-catch。
- 启动类在 `com.kyoo.mall.app` 包（app 模块），通过 `scanBasePackages = "com.kyoo.mall"` 显式覆盖所有模块（默认只扫启动类所在包，漏配会导致业务模块 Bean 全部丢失）；Mapper 由 `@MapperScan("com.kyoo.mall.**.infrastructure.persistence.mapper")` 扫描（Mapper 单独放 mapper 子包，只被 RepositoryImpl 使用）。
- **JWT 签发在 user 模块**（`infrastructure/security/JwtTokenProvider`），**安全规则在 app 模块**（`interfaces/SecurityConfig`：过滤器链、放行路径属 Web 入站关注点）。`/auth/**` 与商品 GET 匿名，其余需登录。
- 表结构变更同步维护两处：`backend/db/init.sql`（PostgreSQL）与 `kyoo-mall-app/src/main/resources/db/h2-init.sql`（H2 验证 profile）。注意 `user` 是 PostgreSQL 保留字，用户表名为 `sys_user`。

## 前端架构（`frontend/src/`）

- **请求层**：所有请求走 `utils/request.js` 的 axios 实例——自动携带 token、解包 `Result`（非 0 弹错误提示并 reject）、401 自动登出跳登录。页面代码拿到的是 `data` 本体，不要再判断 `code`。
- **状态**：登录态在 `stores/auth.js`（Pinia），持久化到 localStorage；路由守卫在 `router/index.js`，通过 `meta.public` 标记匿名页面。
- **API 封装**：按域放在 `api/` 目录（如 `api/product.js`），页面不直接调 axios。
- UI 使用 Element Plus + 中文 locale（`main.js` 已配置）。

## 前后端联调约定

- 前端请求 `/api/**` 由 Vite 代理到后端（后端 context-path 就是 `/api`，代理不做 rewrite）。
- 登录/注册返回 `{ token, userId, username, nickname }`，前端存入 Pinia 后在请求头携带。
