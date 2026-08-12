# 个人任务清单系统（nrec-service-cli · app 模块）

基于 `nrec-service-cli` 脚手架的 `app` 模块实现的个人任务清单后端 REST API。
仅后端，无前端页面；通过 Swagger2 提供接口文档与联调界面。

---

## 一、技术栈

| 项 | 版本 / 说明 |
|---|---|
| JDK | 1.8（JDK8，不可使用 Java 9+ 语法） |
| Spring Boot | 2.3.12.RELEASE |
| MyBatis-Plus | 3.5.3.2 |
| 安全 | spring-boot-starter-security + jjwt 0.11.5（HS256） |
| 接口文档 | Swagger2（springfox） |
| 参数校验 | OVal（net.sf.oval） |
| 数据库 | MySQL 5.7 / 8.x 通用 |
| 密码存储 | BCrypt 哈希（spring-security `PasswordEncoder`） |
| 统一返回 | 复用脚手架 `com.nrec.base.common.model.Result` |
| 统一异常 | 复用脚手架 `com.nrec.base.common.exception.ServiceException` + `@Profile dev/test` 的 `ExceptionInterceptor` |

> 包名固定为 `com.nrec.service.app`；context-path 为 `/task-app`；运行 profile 为 `test`。
> 主键统一为 `VARCHAR(32)` 的 UUID（MyBatis-Plus `assign_uuid` 策略自动生成）。

---

## 二、分层架构

```
com.nrec.service.app
├── controller        # 仅做参数接收 + 校验触发 + 结果包装，不含业务逻辑
│   ├── AuthController        # /auth/register, /auth/login（公开）
│   ├── UserController        # /users/me, /users/password（需 Token）
│   ├── TaskController        # /tasks/**（需 Token）
│   └── CategoryController    # /categories/**（需 Token）
├── service
│   ├── IAuthService / IUserService / ITaskService / ICategoryService
│   └── impl/*ServiceImpl    # 业务逻辑、跨用户归属校验、异常抛出
├── mapper            # MyBatis-Plus Mapper（继承 SuperMapper）
├── entity            # 数据库实体（task_user / task_category / task_item）
├── model
│   ├── dto          # 对外传输对象（绝不带 password 字段）
│   ├── qo           # 请求对象（含 OVal 校验注解）
│   ├── TaskStatus / TaskStatusCheck  # 状态常量 + 状态校验
│   └── SuperMapper
├── security         # JWT 工具、过滤器、Spring Security 配置、安全上下文
└── common           # ValidationUtils（OVal 触发）、BizCode（异常 code 常量）
```

**严格三层**：Controller → Service → Mapper，业务逻辑只在 Service 层；
Controller 不直连 Mapper，也不编写主要业务逻辑。

---

## 三、数据库表关系

```
task_user(id, username[UK], password[BCrypt], email, enabled, created_at, updated_at)
       │  1
       │
       │  N（user_id 外键，逻辑关联，无物理 FK）
       ├──► task_category(id, name, user_id, created_at)
       │         │ 1
       │         │
       │         │ N（category_id，可空；删除分类时仅置空，不删任务）
       │         └──► task_item(id, title, description, status[0/1/2], due_date,
       │                        user_id, category_id[可空], created_at, updated_at)
       └──► task_item(user_id 直接归属用户)
```

- `task_user.uk_user_username`：用户名唯一。
- `task_category.uk_category_user_name(user_id, name)`：同一用户下分类名唯一。
- `task_item.status`：CHAR(1)，`0` 待办 / `1` 进行中 / `2` 已完成。
- 索引：`idx_category_user(user_id)`、`idx_task_user(user_id)`、
  `idx_task_user_status(user_id, status)`、`idx_task_category(category_id)`。
- 建库与测试数据见 `sql/schema-mysql.sql`、`sql/data-mysql.sql`。

---

## 四、JWT 认证流程

1. **注册** `POST /task-app/auth/register`（公开）
   校验用户名/密码 → 查重 → 用 `PasswordEncoder` 对密码做 BCrypt 哈希落库，`status` 默认启用。
2. **登录** `POST /task-app/auth/login`（公开）
   校验用户名密码（BCrypt `matches`）→ 用 `JwtUtil` 签发 HS256 Token
   （`sub=userId`，`exp=24h`）→ 返回 `{token, expiresAt, user}`。
3. **携带 Token 访问受保护接口**
   请求头 `Authorization: Bearer <token>`。
   `JwtAuthFilter`（在用户名密码过滤器之前）解析 Token，
   将 `userId/username` 写入 `SecurityContext` 与 `SecurityContextUtil` 静态上下文。
4. **服务端强制归属**
   所有 `/tasks`、`/categories`、`/users/me` 操作都从 `SecurityContextUtil` 取
   `当前用户ID`，并以「资源ID + 当前用户ID」双约束查询，
   查不到即抛「数据不存在」，用户 A 无法触达用户 B 的数据。
5. **失败处理**
   - 无 Token / Token 过期 / 篡改 → `RestAuthenticationEntryPoint` 返回 **401**。
   - 已登录但无权限 → `RestAccessDeniedHandler` 返回 **403**。

> JWT 密钥为开发用固定密钥（位于 `application-test.yml` 的
> `nrec.task.jwt.secret`，长度 ≥ 32 字节），生产环境应改为环境变量/配置中心注入。

---

## 五、接口一览（共 13 个）

| 方法 | 路径（前缀 /task-app） | 鉴权 | 说明 |
|---|---|---|---|
| POST | /auth/register | 公开 | 注册 |
| POST | /auth/login | 公开 | 登录，返回 Token |
| GET | /users/me | Token | 当前用户信息 |
| PUT | /users/password | Token | 修改密码（校验原密码） |
| POST | /tasks/page | Token | 任务分页（含分类名、按当前用户过滤） |
| GET | /tasks/{id} | Token | 任务详情 |
| POST | /tasks | Token | 创建任务（状态默认 0） |
| PUT | /tasks/{id} | Token | 更新任务 |
| PUT | /tasks/{id}/status | Token | 修改任务状态 |
| DELETE | /tasks/{id} | Token | 删除任务 |
| GET | /categories | Token | 分类列表（当前用户） |
| POST | /categories | Token | 创建分类 |
| DELETE | /categories/{id} | Token | 删除分类（其下任务仅置空 category_id） |

**白名单（免 Token）**：`/auth/**`、`/swagger-ui.html`、`/v2/api-docs`、
`/swagger-resources/**`、`/webjars/**`、`/doc.html`、`/error`、`/favicon.ico`。

**异常语义（code + 中文 msg）**：
`PARAM_ERROR`（参数错误/未登录）、`NOT_FOUND`（数据不存在）、
`DUPLICATE_USER`（用户名重复）、`DUPLICATE_CATEGORY`（分类名重复）、
`PWD_WRONG`（原密码错误）、`INVALID_STATUS`（状态非法）。
未登录=401、无权限=403 由 Security 直接返回。

---

## 六、本地运行与测试

### 1. 初始化数据库
```sql
-- 在 MySQL 客户端执行（已自带 CREATE DATABASE）
source sql/schema-mysql.sql;   -- 建库 + 三表 + 约束 + 索引
source sql/data-mysql.sql;     -- 可选：测试数据（user_a / user_b，密码 Test@123456）
```

### 2. 配置数据库连接
`app/src/main/resources/application-test.yml` 中：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/task_app?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: ${TASK_DB_PASSWORD}   # 本地用环境变量注入，切勿落库明文
```
> 本仓库 `password` 一律为 `${TASK_DB_PASSWORD}` 占位符，不含任何真实密码。

### 3. 启动

> ⚠️ 本项目父 POM（nrcloud-fat）在 `spring-boot-maven-plugin` 根级配了
> `excludeGroupIds=org.springframework,org.springframework.security`，
> 对 `spring-boot:run` 目标也生效，会导致运行时 `NoClassDefFoundError`。
> **因此 `spring-boot:run` 不可用**，必须用 fat jar 方式启动。

**方式一：命令行（推荐）**
```bash
cd app
mvn -Ptest package -DskipTests
set TASK_DB_PASSWORD=&lt;你的本地MySQL密码&gt;        # 本地数据库 root 密码，仅环境变量注入
java -jar target/app.jar
```

**方式二：IDEA**
- 运行 `AppApplication`
- VM options：`-Dspring.profiles.active=test -DTASK_DB_PASSWORD=&lt;你的本地MySQL密码&gt;`

### 4. 接口文档 / 联调
启动后访问：
- Swagger UI：`http://localhost:8080/task-app/swagger-ui.html`
- 文档 JSON：`http://localhost:8080/task-app/v2/api-docs`

**联调顺序建议**：
1. `POST /auth/register` 注册两个用户 →
2. `POST /auth/login` 取 Token →
3. 在 Swagger 右上角「Authorize」填入 `Bearer <token>` →
4. 依次测试 tasks / categories 增删改查；
5. 用用户 A 的 Token 尝试访问/修改用户 B 的数据，应返回「数据不存在」/401。

