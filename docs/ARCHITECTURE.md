# Kyoo Mall 目录架构规范

> **本文档是强制约定，所有新增代码必须遵从。**
> 后端的核心依赖/分层规则由 ArchUnit 自动化校验（`backend/kyoo-mall-app/src/test/java/.../ArchitectureTest.java`），
> 违反规则 `mvn test` / `mvn package` 会直接失败。无法自动校验的部分（命名、归属判断）以本文档为准，Code Review 时检查。

## 1. 顶层结构

```
kyoo-mall/
├── backend/             # Maven 多模块后端（详见 §2）
├── frontend/            # Vue3 前端（详见 §4）
├── docs/                # 项目文档（本文件所在）
├── docker-compose.yml   # 本地基础设施
└── README.md
```

新增顶层目录需要先在本文档登记其用途。

## 2. 后端模块划分

```
backend/
├── pom.xml               # 父工程：模块清单 + 统一依赖版本，不放任何代码
├── db/                   # 数据库脚本（init.sql 等，与代码解耦，供 DBA/运维使用）
├── kyoo-mall-common/     # 共享内核
├── kyoo-mall-<域>/       # 每个限界上下文一个模块（user、product、...）
└── kyoo-mall-app/        # 组装层/启动模块
```

### 2.1 模块职责与依赖矩阵

| 模块 | 职责 | 允许依赖 |
|---|---|---|
| `kyoo-mall-common` | 跨上下文共享的原语：`Result`、`BusinessException`、`BaseEntity`（审计字段基类）。**禁止**放业务概念、工具类堆积 | lombok + MP 纯注解包（零 Spring 依赖） |
| `kyoo-mall-<域>` | 一个限界上下文的全部代码（如 user = 注册/登录/令牌） | common + 技术框架 |
| `kyoo-mall-app` | 组装层：启动类、Security 规则、全局异常处理、`application*.yml`、架构测试 | 所有业务模块 |

**铁律：**

1. `app → 业务模块 → common`，**业务模块之间禁止互相依赖**（Maven 层面就不允许加）。
2. 跨上下文的协作（如订单需要商品信息）：当前阶段在 **app 组装层编排**两个模块的应用服务；拆微服务时改为 RPC/事件。禁止为了图方便在 pom 里加业务模块间依赖。
3. 新增限界上下文 = 新建 `kyoo-mall-<域>` 模块（复制 product 模块的 pom 与包结构），并登记到父 pom 和本文档。
4. `kyoo-mall-common` 只放真正的共享内核。拿不准放 common 还是某模块时，放模块里——common 宁可小不可杂。

### 2.2 模块内的 DDD 四层（每个业务模块必须严格遵守）

```
com.kyoo.mall.<域>/
├── interfaces/                    # 接入层：所有入站适配器
│   ├── XxxController.java         #   HTTP 入站
│   ├── consumer/                  #   MQ 消费者（入站，用到才建）+ message/ 消息体
│   ├── job/                       #   定时任务（入站，用到才建）
│   ├── dto/                       #   XxxRequest / XxxResponse（record，HTTP 协议模型）
│   └── assembler/                 #   DTO ↔ Command/Result 转换（可选）
├── application/                   # 应用层：用例编排、事务边界
│   ├── service/XxxAppService.java #   一个聚合一个应用服务
│   ├── command/                   #   写用例入参（record，用例间不复用）
│   ├── query/                     #   读用例查询条件（可选）
│   ├── result/XxxResult.java      #   应用层返回对象（record，可选）
│   ├── listener/                  #   领域事件订阅（可选，只做跨聚合编排）
│   └── gateway/                   #   出站端口接口 + dto/（技术无关，可选）
├── domain/                        # 领域层：业务核心，零 Spring 依赖
│   ├── model/Xxx.java             #   领域对象（务实起见兼任持久化对象，见 §2.4）
│   │                              #   业务枚举/常量跟着聚合走，禁止集中式 enums/ 目录
│   ├── event/                     #   领域事件（不可变契约，与 model 平级）
│   ├── repository/XxxRepository.java   # 仓储接口 + query/ 查询条件对象
│   └── service/XxxService.java    #   领域服务接口（如 TokenProvider）+ param/ 参数对象
└── infrastructure/                # 基础设施层：技术实现细节
    ├── persistence/
    │   ├── mapper/XxxMapper.java  #   MP Mapper（@MapperScan 精确指向 mapper 子包，
    │   │                          #     只允许被 XxxRepositoryImpl 使用，ArchUnit 校验）
    │   ├── po/                    #   持久化对象（可选，模型与表分离时用）
    │   └── XxxRepositoryImpl.java #   仓储实现（不进 @MapperScan 范围）
    ├── messaging/                 #   MQ 生产者实现 + message/（用到才建）
    ├── client/                    #   外部服务客户端（实现 application/gateway，用到才建）
    ├── config/                    #   本模块的 @ConfigurationProperties（可选）
    └── <技术点>/                  #   如 security/、util/
```

子包渐进原则：**用到才建**（无 MQ 不建 consumer/messaging）；application 单域 <10 类时可扁平，10+ 再按类型分包。

### 2.3 各层允许/禁止（ArchUnit 自动校验）

| 层 | 允许依赖 | 禁止依赖 |
|---|---|---|
| `domain` | 自身、common、lombok、JDK、MyBatis-Plus **注解**、Jackson 注解 | `application`、`interfaces`、`infrastructure`、Spring 任何包 |
| `application` | `domain`、common、Spring（`@Service`/`@Transactional` 等） | `interfaces`、`infrastructure` |
| `interfaces` | `application`、`domain`（读模型）、common、Spring Web/Validation | `infrastructure` |
| `infrastructure` | `domain`（实现其接口）、common、任何技术框架 | `interfaces`、`application` |

补充规则（人审）：

- Controller 只做：参数校验（`@Valid`）→ 调用一个应用服务方法 → 组装 `Result<XxxResponse>`。**禁止**在 Controller 写业务判断、直接调仓储/Mapper。
- 应用服务之间可以同模块内调用；跨模块调用走 §2.1 第 2 条。
- 业务异常一律抛 `BusinessException`（common），由 app 层 `GlobalExceptionHandler` 统一转 `Result`。禁止 Controller 手写 try-catch 转响应。
- 审计字段（`createTime`/`updateTime`）统一定义在 common 的 `BaseEntity`，实体一律继承、禁止重复声明；字段值由 app 模块 `AuditMetaObjectHandler`（MP MetaObjectHandler）在 insert/update 时自动填充，业务代码（含聚合工厂方法）禁止手动 set。

### 2.4 命名约定

| 类型 | 命名 | 示例 |
|---|---|---|
| Controller | `XxxController` | `ProductController` |
| 应用服务 | `XxxAppService`（放 application/service） | `ProductAppService` |
| 写用例入参 | `XxxCommand`（record，放 application/command，不复用） | `CreateProductCommand` |
| 读用例查询条件 | `XxxQuery`（放 application/query 或 domain/repository/query） | `OrderPageQuery` |
| 领域对象 | 名词本身，不加后缀 | `Product`、`SysUser` |
| 仓储 | `XxxRepository` / `XxxRepositoryImpl` | `UserRepository` / `UserRepositoryImpl` |
| MyBatis-Plus Mapper | `XxxMapper`，放 `persistence/mapper/`，只被 `XxxRepositoryImpl` 使用 | `SysUserMapper` |
| 请求/响应 DTO | `XxxRequest` / `XxxResponse`（record，放 interfaces/dto） | `LoginRequest` |
| 应用层返回对象 | `XxxResult`（record，放 application/result） | `LoginResult` |
| 出站端口 | `XxxGateway` + `dto/XxxParam`、`XxxResult`（application/gateway） | `SmsGateway` |
| 第三方协议模型 | `XxxApiRequest` / `XxxApiResponse`（infrastructure/client/<服务>/dto） | `AliyunSmsRequest` |
| 表名 | 小写下划线；避开 PostgreSQL 保留字（如用户表 `sys_user`） | `product` |

**三套 DTO 互不复用**：`interfaces/dto`（对外 HTTP）、`application/gateway/dto`（出站端口契约，技术无关）、`infrastructure/client/dto`（第三方 wire 格式）。字段相同也不合并，重复是有意的隔离。

### 2.5 配置与资源归属

| 内容 | 位置 |
|---|---|
| `application.yml`、profile 配置（`application-h2.yml`） | 仅 `kyoo-mall-app/src/main/resources/` |
| H2 验证库脚本 | `kyoo-mall-app/src/main/resources/db/h2-init.sql` |
| PostgreSQL 正式脚本 | `backend/db/init.sql`（docker-compose 挂载） |
| `@Configuration` 配置类 | 默认放 `app` 模块（`app/config`）；某模块确需自带配置时放自己的 `infrastructure` 并在注释说明理由 |
| 表结构变更 | **必须同步改两处**：`backend/db/init.sql` 和 H2 脚本 |

### 2.6 测试归属

- 单元测试放被测代码所在模块的 `src/test/java`（同包结构）。
- 架构守护测试集中在 `kyoo-mall-app/src/test/java/com/kyoo/mall/app/ArchitectureTest.java`，新模块/新规则要同步补充。

### 2.7 反例速查

```
❌ Controller 里 @Autowired XxxMapper 直接查库          → Mapper 只能被 RepositoryImpl 用（ArchUnit 校验）
❌ Controller 用领域对象当 @RequestBody 接收            → 写接口必须走 XxxRequest + XxxCommand，id/status/时间戳由服务端管理
❌ application 层 import infrastructure 的 Mapper/Impl  → 只能注入 domain 的仓储接口
❌ order 模块 pom 里依赖 product 模块                   → 业务模块间禁止依赖，在 app 层编排
❌ domain 模型上标 @Service / 注入 Spring Bean          → domain 零 Spring 依赖
❌ 把商品相关的常量放进 common                          → common 只放跨域原语
❌ 新建 XxxUtil 无处安放就丢 common                     → 属于哪个域就放哪个域
```

## 3. 后端拆分微服务的预案（约束的出发点）

- 每个 `kyoo-mall-<域>` 模块 + 一个新的启动模块即可独立成服务；接口路径已按域隔离（`/auth/**`、`/products/**`）。
- token 签发在 user 上下文，其余服务用 `JwtTokenProvider.parse` 校验（届时抽到 common-security 或走网关）。
- 数据库按上下文分表归属：每个模块只读写自己的表（user → `sys_user`，product → `product`），**禁止跨模块写 SQL 连别人的表**。

## 4. 前端目录规范

```
frontend/src/
├── api/          # 按域划分的 API 封装（auth.js、product.js），只调 utils/request.js
├── utils/        # request.js（axios 唯一实例）等纯工具，不含业务
├── stores/       # Pinia store（auth.js），跨页面共享状态才放这里
├── router/       # 路由与守卫
├── views/        # 页面级组件，一页面一文件，XxxView.vue
├── components/   # 跨页面复用的组件（页面私有组件放该页面文件内或同名目录）
├── assets/       # 静态资源
└── App.vue / main.js
```

规则：

1. **页面禁止直接 import axios**，一律通过 `api/` 封装；`request.js` 已统一处理 token、Result 解包、401。
2. 页面拿到的是 `Result.data` 本体，**不再判断 `code`**。
3. 新增页面：路由登记到 `router/index.js`，匿名页面加 `meta: { public: true }`，默认全部需登录。
4. UI 一律使用 Element Plus 组件 + 已配置的中文 locale；图标用 `@element-plus/icons-vue`。
5. 路径别名 `@` = `src/`，禁止写 `../../` 长相对路径跨目录引用。
