# SpringBoot 企业级快速开发模板

> 开箱即用的 SpringBoot 企业级项目模板，预置企业开发中常用的依赖、配置、工具类与基础设施代码，让团队跳过重复的脚手架搭建工作，直接进入业务开发。

---

## 一、项目简介

在企业级 Java 项目开发中，每个新项目几乎都要重复搭建一套基础设施：统一响应格式、全局异常处理、权限认证、日志记录、缓存管理、接口文档……这些与业务无关的公共代码占据了项目启动阶段大量时间。

本模板将这些企业级通用能力打包为一套标准化的项目骨架，提供：

- **标准化的项目结构** — 分层清晰，模块职责明确
- **预置的通用配置** — 多环境配置、数据库连接池、Redis、日志等一键就绪
- **开箱即用的基础组件** — 统一响应、异常处理、参数校验、分页、代码生成器
- **安全认证体系** — Spring Security + JWT，RBAC 权限模型
- **开发效率工具** — 接口文档、代码生成器、热部署、Lombok

## 二、技术栈

| 分类       | 技术选型                  | 版本     | 说明                                 |
| ---------- | ------------------------- | -------- | ------------------------------------ |
| 基础框架   | Spring Boot               | 4.0.x    | 基于 Spring Framework 7，模块化启动 |
| Java 版本  | JDK                       | 17       | LTS 版本                             |
| 构建工具   | Maven                     | 3.9+     | 多环境 profile 支持                  |
| ORM 框架   | MyBatis-Plus              | 3.5.17   | Spring Boot 4 专用 starter + jsqlparser |
| 数据库     | MySQL                     | 8.0+     | 企业主流数据库                       |
| 连接池     | HikariCP                  | 内置     | Spring Boot 默认连接池               |
| 缓存       | Redis + Redisson          | 4.7.0    | 分布式缓存与分布式锁（社区版）       |
| 安全框架   | Spring Security + JWT    | 7.0+     | 认证与授权，无状态 JWT               |
| 接口文档   | springdoc-openapi        | 3.1.1    | 支持 Spring Boot 4 + OpenAPI 3       |
| 参数校验   | Hibernate Validator       | 8.0+     | JSR 380 Bean Validation              |
| 对象映射   | MapStruct                 | 1.6.3    | 编译期生成，配合 Lombok 绑定         |
| 工具类库   | Hutool                    | 5.8.47   | 国产工具类大全                       |
| JWT 库     | JJWT (jjwt-api)           | 0.12.6   | JWT 生成与解析三件套                 |
| 日志框架   | Logback                   | 内置     | 支持文件分割与多环境 profile         |
| 消息队列   | RabbitMQ（可选）          | 3.12+    | 异步解耦                             |
| 数据库迁移 | Flyway                    | 内置     | 版本化管理 SQL 脚本                  |
| 容器化     | Docker + Docker Compose   | 24+      | 一键部署中间件与应用                 |
| 监控       | Spring Boot Actuator      | 内置     | 健康检查与指标暴露                    |

## 三、项目结构

```
SpringBoot-Template
├── pom.xml                              # 父 POM，统一依赖版本管理
├── README.md                            # 项目说明文档
├── docker-compose.yml                   # 中间件一键启动（MySQL/Redis/RabbitMQ）
├── Dockerfile                           # 应用镜像构建
│
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── SpringBootTemplateApplication.java  # 启动类
│   │   │   │
│   │   │   ├── config/                         # 配置类
│   │   │   │   ├── MybatisPlusConfig.java       #   MyBatis-Plus 配置（分页/乐观锁/自动填充）
│   │   │   │   ├── RedisConfig.java            #   Redis 配置（Jackson 3 序列化）
│   │   │   │   ├── SecurityConfig.java         #   Spring Security + JWT + CORS
│   │   │   │   ├── SpringDocConfig.java        #   OpenAPI 3 接口文档配置
│   │   │   │   ├── JwtProperties.java          #   JWT 配置属性绑定类
│   │   │   │   ├── WebMvcConfig.java           #   Web MVC 配置
│   │   │   │   ├── AsyncConfig.java            #   异步线程池配置
│   │   │   │   └── ...
│   │   │   │
│   │   │   ├── common/                         # 公共模块
│   │   │   │   ├── core/                        #   核心基础类
│   │   │   │   │   └── BaseEntity.java         #     实体基类（id, createTime...）
│   │   │   │   ├── exception/                   #   异常处理
│   │   │   │   │   ├── BusinessException.java   #     业务异常
│   │   │   │   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │   │   │   ├── result/                      #   统一响应
│   │   │   │   │   ├── R.java                   #     统一返回体
│   │   │   │   │   ├── ResultCode.java          #     状态码枚举
│   │   │   │   │   └── PageResult.java          #     分页返回体
│   │   │   │   ├── constant/                    #   常量定义
│   │   │   │   ├── enums/                       #   通用枚举
│   │   │   │   └── annotation/                  #   自定义注解
│   │   │   │       ├── @RateLimit.java          #     限流注解
│   │   │   │       ├── @OperLog.java            #     操作日志注解
│   │   │   │       └── @DataScope.java          #     数据权限注解
│   │   │   │
│   │   │   ├── security/                       # 安全认证
│   │   │   │   ├── JwtUtils.java               #   JWT 工具类
│   │   │   │   ├── JwtAuthenticationFilter.java #   JWT 过滤器
│   │   │   │   ├── UserDetailsServiceImpl.java #   用户认证实现
│   │   │   │   └── SecurityUtils.java          #   获取当前登录用户
│   │   │   │
│   │   │   ├── aspect/                         # AOP 切面
│   │   │   │   ├── OperLogAspect.java          #   操作日志切面
│   │   │   │   └── RateLimitAspect.java        #   限流切面（Redis Lua 脚本）
│   │   │   │
│   │   │   ├── modules/                        # 业务模块（示例）
│   │   │   │   └── system/                     #   系统管理模块
│   │   │   │       ├── controller/
│   │   │   │       ├── service/
│   │   │   │       ├── mapper/
│   │   │   │       ├── entity/
│   │   │   │       ├── dto/
│   │   │   │       └── vo/
│   │   │   │
│   │   │   └── utils/                          # 工具类
│   │   │       ├── RedisUtils.java             #   Redis 操作工具
│   │   │       └── SpringUtils.java            #   Spring 上下文工具
│   │   │
│   │   └── resources/
│   │       ├── application.yml                 # 主配置文件
│   │       ├── application-dev.yml             #   开发环境
│   │       ├── application-test.yml            #   测试环境
│   │       ├── application-prod.yml            #   生产环境
│   │       ├── logback-spring.xml              #   日志配置（多环境 profile）
│   │       ├── mapper/                         #   MyBatis XML 映射文件
│   │       └── db/migration/                    #   Flyway SQL 脚本
│   │           └── V1.0.0__init_schema.sql
│   │
│   └── test/java/com/example/                  # 单元测试
│
└── sql/
    └── template.sql                            # 初始化 SQL 脚本
```

## 四、核心特性

### 4.1 统一响应格式

所有接口返回统一的 `R<T>` 结构，前端无需适配不同格式：

```java
@GetMapping("/info/{id}")
public R<UserVO> getInfo(@PathVariable Long id) {
    return R.ok(userService.getById(id));
}
```

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "username": "admin"
  }
}
```

### 4.2 全局异常处理

业务异常只需 `throw new BusinessException("xxx")`，无需手写 try-catch，统一由全局处理器捕获并返回标准格式。

### 4.3 JWT 认证与权限

- 登录签发 JWT Token，请求头携带 Token 完成认证
- 基于 RBAC 模型：用户 — 角色 — 权限
- 接口级权限控制：`@PreAuthorize("@perm.has('sys:user:list')")`
- 数据权限：`@DataScope` 注解实现查询数据范围控制

### 4.4 操作日志

通过 `@OperLog` 注解自动记录操作日志，无需手写日志代码：

```java
@OperLog(value = "用户管理", type = OperType.INSERT)
@PostMapping
public R<Void> add(@RequestBody UserDTO dto) {
    userService.save(dto);
    return R.ok();
}
```

### 4.5 接口限流

基于 Redis 的分布式限流，防止恶意请求：

```java
@RateLimit(key = "login", time = 60, count = 5)
@PostMapping("/login")
public R<String> login(@RequestBody LoginDTO dto) { ... }
```

### 4.6 代码生成器

内置 MyBatis-Plus 代码生成器，输入表名即可一键生成 Entity、Mapper、Service、Controller 全套代码，大幅减少重复劳动。

### 4.7 接口文档

集成 springdoc-openapi（支持 Spring Boot 4 + OpenAPI 3），启动后访问 `http://localhost:8080/swagger-ui.html` 即可查看在线接口文档，支持 JWT 认证调试。

### 4.8 多环境配置

通过 Spring Profile 实现多环境隔离：

```bash
# 开发环境（默认）
java -jar app.jar

# 生产环境
java -jar app.jar --spring.profiles.active=prod

# 测试环境
java -jar app.jar --spring.profiles.active=test
```

## 五、快速开始

### 5.1 环境要求

| 环境  | 版本要求  |
| ----- | --------- |
| JDK   | 17+       |
| Maven | 3.9+      |
| MySQL | 8.0+      |
| Redis | 6.0+      |

### 5.2 启动中间件

使用 Docker Compose 一键启动 MySQL 和 Redis：

```bash
docker-compose up -d
```

### 5.3 初始化数据库

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE template DEFAULT CHARACTER SET utf8mb4;"

# 执行初始化脚本
mysql -u root -p template < sql/template.sql
```

### 5.4 修改配置

修改 `application-dev.yml` 中的数据库和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/template?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

### 5.5 启动项目

```bash
# 克隆项目
git clone <repository-url>

# 进入项目目录
cd springboot-enterprise-template

# 编译打包
mvn clean package -DskipTests

# 运行
java -jar target/template.jar
```

启动成功后访问：
- 接口文档：http://localhost:8080/swagger-ui.html
- 健康检查：http://localhost:8080/actuator/health

## 六、Docker 部署

```bash
# 构建镜像
docker build -t enterprise-template:latest .

# 运行容器
docker run -d \
  --name enterprise-app \
  -p 8080:8080 \
  --env SPRING_PROFILES_ACTIVE=prod \
  enterprise-template:latest
```

## 七、开发规范

### 7.1 分层规范

| 层级       | 命名规范                 | 职责                               |
| ---------- | ------------------------ | ---------------------------------- |
| Controller | `XxxController`          | 接口入参校验、调用 Service、返回结果 |
| Service    | `XxxService` / `XxxServiceImpl` | 业务逻辑处理                 |
| Mapper     | `XxxMapper`              | 数据库操作                         |
| Entity     | `Xxx`                    | 数据库实体映射                     |
| DTO        | `XxxDTO`                 | 接口入参对象                       |
| VO         | `XxxVO`                  | 接口出参对象                       |

### 7.2 命名规范

- 类名：大驼峰 `UserService`
- 方法名/变量名：小驼峰 `getUserById`
- 常量：全大写下划线 `MAX_PAGE_SIZE`
- 数据库表名/字段名：小写下划线 `sys_user`

### 7.3 接口规范

- RESTful 风格：`GET /users` 查询、`POST /users` 新增、`PUT /users` 修改、`DELETE /users/{id}` 删除
- 统一返回 `R<T>` 格式
- 分页查询统一使用 `PageResult<T>`
- 所有接口添加 springdoc OpenAPI 注解

## 八、后续规划

- [ ] 多数据源动态切换
- [x] 消息队列集成（RabbitMQ）
- [ ] 分布式事务（Seata）
- [ ] ELK 日志收集
- [ ] Prometheus + Grafana 监控面板
- [ ] MinIO 文件存储服务
- [ ] WebSocket 实时通信
- [ ] 代码生成器（MyBatis-Plus Generator）

## 九、版本记录

| 版本  | 日期       | 说明                    |
| ----- | ---------- | ----------------------- |
| 1.0.0 | 2026-09-19 | 初始版本，基础骨架搭建  |

---

> 如果本模板对你有帮助，欢迎 Star 支持。
