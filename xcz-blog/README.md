# Xcz Blog Backend - 后端服务

<div align="center">

![Java](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=spring-boot)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.0-green?logo=spring-cloud)
![MyBatis Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.9-orange)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

</div>

## 📖 项目简介

Xcz Blog Backend 是基于 Spring Boot + Spring Cloud Alibaba 构建的博客系统后端服务，采用微服务模块化架构设计。项目包含用户服务、文章服务、网关服务以及多个公共模块（安全、缓存、数据源等），支持高并发、分布式部署。

### ✨ 主要特性

- 🏗️ **微服务架构** - Spring Cloud Alibaba + Nacos 服务治理
- 🔐 **安全认证** - Spring Security + JWT 双重认证
- 🚀 **高性能缓存** - Redis + Redisson 多级缓存
- 📊 **读写分离** - Dynamic DataSource 动态数据源
- 🔍 **全文检索** - Elasticsearch 搜索引擎
- 📦 **消息队列** - RocketMQ 异步解耦
- 🎯 **ID 生成** - 分布式 ID 生成器
- 🛡️ **安全防护** - XSS 过滤、SQL 注入防护、限流控制

## 🏗️ 项目结构

```
xcz-blog/
├── xcz-gateway/                    # 网关服务 (端口：8080)
│   ├── src/main/java/com/xcz/
│   │   ├── gateway/                # 网关配置
│   │   └── DimpleGatewayApplication.java
│   └── pom.xml
│
├── xcz-blog-user/                  # 用户服务模块 (端口：8081)
│   ├── src/main/java/com/xcz/
│   │   ├── controller/             # 用户控制器
│   │   ├── mapper/                 # 数据访问层
│   │   ├── proto/                  # 协议定义
│   │   ├── service/                # 业务逻辑层
│   │   └── XczBlogUserApplication.java
│   └── pom.xml
│
├── xcz-blog-article/               # 文章服务模块 (端口：8082)
│   ├── src/main/java/com/xcz/
│   │   └── article/                # 文章相关业务
│   │       ├── controller/         # 文章控制器
│   │       ├── mapper/             # 数据访问层
│   │       ├── model/              # 数据模型
│   │       ├── service/            # 业务逻辑层
│   │       └── vo/                 # 视图对象
│   └── pom.xml
│
├── xcz-common-core/                # 核心工具包
│   ├── annotation/                 # 自定义注解
│   ├── config/                     # 核心配置
│   ├── constant/                   # 常量定义
│   ├── domain/                     # 领域模型
│   ├── enums/                      # 枚举类
│   ├── exception/                  # 异常处理
│   ├── text/                       # 文本处理
│   ├── utils/                      # 工具类
│   └── xss/                        # XSS 防护
│
├── xcz-common-security/            # 安全认证模块
│   ├── annotation/                 # 安全注解
│   ├── aspect/                     # 安全切面
│   ├── config/                     # 安全配置
│   ├── extend/                     # 安全扩展
│   ├── feign/                      # Feign 拦截器
│   ├── handler/                    # 认证处理器
│   ├── interceptor/                # 请求拦截器
│   ├── service/                    # 认证服务
│   └── utils/                      # 认证工具
│
├── xcz-common-cache/               # 缓存模块
│   ├── config/                     # Redis 配置
│   ├── extend/                     # 缓存常量
│   └── utils/                      # Redis 工具
│
├── xcz-common-datasource/          # 数据源配置模块
│   ├── config/                     # 数据源配置
│   └── extend/                     # 数据源扩展
│
├── xcz-common-id/                  # ID 生成器模块
│   ├── repository/                 # ID 仓库
│   │   ├── entity/                 # ID 实体
│   │   └── mapper/                 # ID 映射
│   └── util/                       # ID 生成工具
│
└── xcz-common-rocketMq/           # RocketMQ 消息队列模块
    ├── config/                     # MQ 配置
    └── utils/                      # MQ 工具
```

## 🛠️ 技术栈

### 核心框架

| 技术 | 版本 | 描述 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.2.0 | 应用框架 |
| Spring Cloud | 2023.0.0 | 微服务框架 |
| Spring Cloud Alibaba | 2023.0.0.0-RC1 | Alibaba 微服务生态 |

### 数据存储

| 技术 | 版本 | 描述 |
|------|------|------|
| MyBatis Plus | 3.5.9 | ORM 框架 |
| MySQL Connector | 8.0.31 | MySQL 驱动 |
| MongoDB Driver | 5.2.1 | MongoDB 驱动 |
| Druid | 4.3.1 | 数据库连接池 |
| PageHelper | 2.1.0 | 分页插件 |

### 缓存与搜索

| 技术 | 版本 | 描述 |
|------|------|------|
| Redisson | 3.50.0 | Redis 客户端 |
| Elasticsearch | 8.11.1 | 全文搜索引擎 |

### 安全与认证

| 技术 | 版本 | 描述 |
|------|------|------|
| JJWT | 0.12.6 | JWT 令牌库 |
| Kaptcha | 2.3.3 | 验证码生成 |

### 工具库

| 技术 | 版本 | 描述 |
|------|------|------|
| Lombok | 1.18.30 | 代码简化 |
| FastJSON2 | 2.0.52 | JSON 解析 |
| Orika | 1.5.4 | Bean 拷贝 |
| Apache POI | 5.2.5 | Excel 处理 |
| Commons FileUpload | 1.5 | 文件上传 |

### 消息队列

| 技术 | 版本 | 描述 |
|------|------|------|
| RocketMQ | - | 分布式消息中间件 |

## 🚀 快速开始

### 环境要求

- JDK: 21+
- Maven: 3.6+
- MySQL: 8.0+
- MongoDB: 6.0+
- Redis: 7.0+
- Nacos: 2.2+
- Elasticsearch: 8.x (可选)
- RocketMQ: 4.x (可选)

### 编译打包

```bash
cd xcz-blog

# 清理编译
mvn clean

# 编译打包（跳过测试）
mvn package -DskipTests

# 安装到本地仓库
mvn install -DskipTests
```

### 启动服务

#### 1. 启动网关服务

```bash
java -jar xcz-gateway/target/xcz-gateway-dev-1.0-SNAPSHOT.jar
```

访问：`http://localhost:8080`

#### 2. 启动用户服务

```bash
java -jar xcz-blog-user/target/xcz-blog-user-dev-1.0-SNAPSHOT.jar
```

服务端口：`8081`

#### 3. 启动文章服务

```bash
java -jar xcz-blog-article/target/xcz-blog-article-dev-1.0-SNAPSHOT.jar
```

服务端口：`8082`

### 多环境配置

项目支持多环境配置切换：

```bash
# 开发环境
mvn package -P dev

# 本地环境
mvn package -P local

# 预发布环境
mvn package -P pre

# 生产环境
mvn package -P prod
```

## 📋 模块详解

### 1. 网关服务 (`xcz-gateway`)

**功能**：
- 统一入口路由
- 身份认证过滤
- 限流控制
- XSS 防护
- 日志记录

**配置文件**：
```yaml
server:
  port: 8080
  
spring:
  cloud:
   nacos:
      discovery:
        server-addr: 127.0.0.1:8848
```

### 2. 用户服务 (`xcz-blog-user`)

**功能**：
- 用户注册/登录
- 用户信息管理
- 角色权限管理
- Token 签发验证

**Controller 示例**：
```java
@RestController
@RequestMapping("/user")
public class UserController {
    
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO dto) {
        return Result.ok(userService.login(dto));
    }
    
    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserRegisterDTO dto) {
        userService.register(dto);
        return Result.ok();
    }
}
```

### 3. 文章服务 (`xcz-blog-article`)

**功能**：
- 文章 CRUD
- 文章分类/标签
- 评论管理
- 文章统计
- 全文检索

**Controller 示例**：
```java
@RestController
@RequestMapping("/article")
public class ArticleController {
    
    @GetMapping("/list")
    public Result<IPage<ArticleVO>> getArticleList(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size
    ) {
        return Result.ok(articleService.getArticleList(page, size));
    }
    
    @PostMapping("/publish")
    public Result<Long> publishArticle(@RequestBody ArticlePublishDTO dto) {
        return Result.ok(articleService.publishArticle(dto));
    }
}
```

### 4. 公共核心模块 (`xcz-common-core`)

**功能**：
- 通用工具类
- 统一异常处理
- 统一响应封装
- 自定义注解
- XSS 防护

**Result 统一响应**：
```java
@Data
public class Result<T> {
   private Integer code;
   private String message;
   private T data;
    
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }
    
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
}
```

### 5. 安全认证模块 (`xcz-common-security`)

**功能**：
- JWT Token 生成与验证
- Spring Security 配置
- Feign 请求拦截
- 权限注解支持

**使用示例**：
```java
@RequiresLogin
@GetMapping("/userInfo")
public Result<UserInfoVO> getUserInfo() {
    Long userId = SecurityUtils.getUserId();
    return Result.ok(userService.getUserInfo(userId));
}
```

### 6. 缓存模块 (`xcz-common-cache`)

**功能**：
- Redis 配置
- Redisson 客户端管理
- 缓存工具类

**使用示例**：
```java
@Autowired
private RedisTemplate<String, Object> redisTemplate;

// 写入缓存
redisTemplate.opsForValue().set("key", value, 30, TimeUnit.MINUTES);

// 读取缓存
Object value = redisTemplate.opsForValue().get("key");
```

### 7. 数据源模块 (`xcz-common-datasource`)

**功能**：
- 动态数据源配置
- 读写分离
- 多数据源切换

**使用示例**：
```java
@DS("slave")
public List<User> queryUsers() {
    return userMapper.selectList(null);
}
```

### 8. ID 生成器模块 (`xcz-common-id`)

**功能**：
- 分布式 ID 生成
- 雪花算法
- 用户 ID 生成

**使用示例**：
```java
Long userId = UserIdUtil.generateUserId();
Long id = IdGenerateManager.nextId();
```

### 9. 消息队列模块 (`xcz-common-rocketMq`)

**功能**：
- RocketMQ 配置
- 消息发送
- 消息消费

**使用示例**：
```java
@Resource
private RocketMQTemplate rocketMQTemplate;

// 发送消息
rocketMQTemplate.convertAndSend("topic:test", message);
```

## ⚙️ 配置说明

### Nacos 配置中心

所有服务都通过 Nacos 进行配置管理：

```yaml
spring:
  config:
    import: "nacos:${spring.application.name}-${spring.profiles.active}.yaml"
  cloud:
   nacos:
     config:
        server-addr: 127.0.0.1:8848
       namespace: public
        group: BLOG_GROUP
```

### 数据库配置

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/blog_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
   password: root
    
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### Redis 配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
   password: 
    database: 0
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
```

### MongoDB 配置

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/blog_db
```

## 🔒 安全机制

### JWT Token 认证

```java
String token = JwtUtils.createToken(userId, username, expiration);
Boolean isValid = JwtUtils.validateToken(token);
Long userId = JwtUtils.getUserIdFromToken(token);
```

### XSS 防护

```java
@XssIgnore
@PostMapping("/content")
public Result<Void> saveContent(@RequestBody String content) {
    // 默认会进行 XSS 过滤
   content = XssUtils.clean(content);
    return Result.ok();
}
```

### 接口限流

```java
@RateLimit(key = "api:user:login", limit = 5, timeout = 60)
@PostMapping("/login")
public Result<UserLoginVO> login(@RequestBody UserLoginDTO dto) {
    return Result.ok(userService.login(dto));
}
```

## 📊 性能优化

### 缓存策略

```java
@Cacheable(value = "article", key = "#id", unless = "#result == null")
public ArticleVO getArticleById(Long id) {
    return articleMapper.selectById(id);
}

@CacheEvict(value = "article", key = "#id")
public void updateArticle(Article article) {
    articleMapper.updateById(article);
}
```

### 分页查询

```java
Page<Article> page = new Page<>(pageNum, pageSize);
IPage<Article> result = articleMapper.selectPage(page, wrapper);
```

### 异步处理

```java
@Async
public void incrementViewCount(Long articleId) {
    redisTemplate.opsForValue().increment("article:view:" + articleId);
}
```

## 🐛 常见问题

### 1. Nacos 连接失败

检查 Nacos 服务是否启动，确认配置正确：
```yaml
spring:
  cloud:
   nacos:
      discovery:
        server-addr: 127.0.0.1:8848
```

### 2. 数据库连接超时

检查数据库连接池配置：
```yaml
spring:
  datasource:
    hikari:
     connection-timeout: 30000
      max-lifetime: 1800000
```

### 3. Redis 连接失败

检查 Redis 服务状态和配置：
```bash
redis-cli ping
# 应返回 PONG
```

## 📝 API 文档

项目集成了 Swagger API 文档，访问地址：
```
http://localhost:8080/doc.html
http://localhost:8081/doc.html
http://localhost:8082/doc.html
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 开源协议

MIT License

---

<div align="center">

**喜欢这个项目吗？请给一个 ⭐️ Star 支持！**

</div>
