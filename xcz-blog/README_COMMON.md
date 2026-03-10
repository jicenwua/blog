# Xcz Common Modules - 公共模块

<div align="center">

![Java](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=spring-boot)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

</div>

## 📖 模块简介

Xcz Common Modules 是博客系统的公共工具模块集合，为各个微服务提供通用的基础设施和工具类。包含核心工具、安全认证、缓存、数据源、ID 生成器、消息队列等模块。

### ✨ 模块列表

| 模块名 | 描述 | 端口 |
|--------|------|------|
| `xcz-common-core` | 核心工具包 | - |
| `xcz-common-security` | 安全认证模块 | - |
| `xcz-common-cache` | 缓存模块 | - |
| `xcz-common-datasource` | 数据源配置模块 | - |
| `xcz-common-id` | ID 生成器模块 | - |
| `xcz-common-rocketMq` | RocketMQ 消息队列模块 | - |

## 🏗️ 模块详解

---

## 1. xcz-common-core - 核心工具包

### 功能特性

- 🔧 **通用工具** - 字符串处理、日期格式化、异常处理
- 📦 **统一响应** - Result 统一返回格式
- 🎯 **自定义注解** - Excel 导出导入、权限注解
- 🛡️ **XSS 防护** - XSS 过滤器、HTML 清理
- 📝 **常量定义** - 系统常量、HTTP 状态码

### 目录结构

```
xcz-common-core/
├── src/main/java/com/xcz/common/core/
│   ├── annotation/              # 自定义注解
│   │   ├── Excel.java         # Excel 导出注解
│   │   └── Excels.java        # Excel 导出集合注解
│   ├── config/                  # 配置类
│   ├── constant/                # 常量类
│   │   ├── Constants.java     # 通用常量
│   │   ├── HttpStatus.java    # HTTP 状态码
│   │   └── UserConstants.java  # 用户常量
│   ├── domain/                  # 领域模型
│   │   └── R.java             # 统一响应
│   ├── enums/                   # 枚举类
│   ├── exception/               # 异常处理
│   │   ├── BusinessException.java  # 业务异常
│   │   └── GlobalExceptionHandler.java
│   ├── text/                    # 文本处理
│   │   ├── CharsetKit.java    # 字符集工具
│   │   ├── Convert.java       # 类型转换
│   │   └── StrFormatter.java  # 字符串格式化
│   ├── utils/                   # 工具类
│   │   ├── DateUtils.java     # 日期工具
│   │   ├── ServletUtils.java  # Servlet 工具
│   │   ├── StringUtils.java   # 字符串工具
│   │   ├── ExceptionUtil.java  # 异常工具
│   │   ├── MessageUtils.java  # 消息工具
│   │   ├── PageUtils.java     # 分页工具
│   │   ├── SpringUtils.java   # Spring 工具
│   │   └── reflect/            # 反射工具
│   ├── web/vo/params/           # 请求参数
│   └── xss/                     # XSS 防护
│       ├── Xss.java           # XSS 注解
│       └── XssValidator.java  # XSS 验证器
│
└── pom.xml
```

### 使用示例

#### 统一响应

```java
@RestController
public class DemoController {
    
    @GetMapping("/demo")
  public Result<String> demo() {
        return Result.ok("success");
    }
    
    @PostMapping("/data")
  public Result<DemoVO> getData() {
        DemoVO data = new DemoVO();
        return Result.ok(data);
    }
    
    @DeleteMapping("/error")
  public Result<Void> error() {
        return Result.error("操作失败");
    }
}
```

#### 业务异常

```java
@Service
public class UserServiceImpl implements UserService {
    
    @Override
  public User getUserById(Long userId) {
        User user = userMapper.selectById(userId);
     if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }
}
```

---

## 2. xcz-common-security - 安全认证模块

### 功能特性

- 🔐 **JWT 认证** - Token 生成、验证、解析
- 🛡️ **Spring Security** - 安全配置、权限管理
- 🎭 **角色权限** - 基于角色的访问控制
- 🔑 **Feign 拦截** - 微服务间认证
- 📝 **日志审计** - 操作日志记录

### 目录结构

```
xcz-common-security/
├── src/main/java/com/xcz/common/security/
│   ├── annotation/              # 安全注解
│   │   ├── RequiresLogin.java  # 登录注解
│   │   └── RequiresRoles.java  # 角色注解
│   ├── aspect/                  # 切面
│   │   └── PermissionAspect.java # 权限切面
│   ├── config/                  # 配置类
│   │   ├── SecurityConfig.java # 安全配置
│   │   ├── JwtConfig.java     # JWT 配置
│   │   └── WebSecurityConfig.java
│   ├── extend/                  # 扩展类
│   │   └── UserDetailsImpl.java # 用户详情实现
│   ├── feign/                   # Feign 组件
│   │   └── FeignInterceptor.java # Feign 拦截器
│   ├── handler/                 # 处理器
│   │   ├── AuthHandler.java   # 认证处理器
│   │   └── LogoutHandler.java  # 登出处理器
│   ├── interceptor/             # 拦截器
│   │   └── AuthInterceptor.java # 认证拦截器
│   ├── service/                 # 服务类
│   │   ├── AuthService.java   # 认证服务
│   │   ├── UserDetailsServiceImpl.java
│   │   └── TokenService.java  # Token 服务
│   └── utils/                   # 工具类
│       ├── JwtUtils.java      # JWT 工具
│       ├── SecurityUtils.java  # 安全工具
│       └── BCryptUtils.java   # BCrypt 加密工具
│
└── pom.xml
```

### 使用示例

#### JWT Token 工具

```java
// 生成 Token
String token = JwtUtils.createToken(userId, username, 86400000L);

// 验证 Token
Boolean isValid = JwtUtils.validateToken(token);

// 解析 Token
Claims claims = JwtUtils.parseToken(token);
Long userId = Long.parseLong(claims.getSubject());
String username= claims.get("username", String.class);

// 获取用户信息
Long currentUserId = SecurityUtils.getUserId();
String currentUsername = SecurityUtils.getUsername();
```

#### 权限注解

```java
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    /**
     * 需要登录访问
     */
    @GetMapping("/user/list")
    @RequiresLogin
  public Result<List<User>> getUserList() {
        // ...
    }
    
    /**
     * 需要管理员角色
     */
    @PostMapping("/user/delete")
    @RequiresRoles("ADMIN")
  public Result<Void> deleteUser(@RequestParam Long userId) {
        // ...
    }
}
```

---

## 3. xcz-common-cache - 缓存模块

### 功能特性

- 💾 **Redis 配置** - RedisTemplate 自动配置
- 🔌 **Redisson 集成** - 分布式锁、缓存
- 📦 **缓存工具** - 常用缓存操作封装
- ⚡ **性能优化** - 连接池优化配置

### 目录结构

```
xcz-common-cache/
├── src/main/java/com/xcz/common/cache/
│   ├── config/                  # 配置类
│   │   └── RedisConfig.java   # Redis 配置
│   ├── extend/                  # 扩展类
│   │   ├── CacheConstants.java # 缓存常量
│   │   ├── RedissonClientBeanEnum.java
│   │   └── RedissonClientEnum.java
│   └── utils/                   # 工具类
│       └── RedissonUtil.java  # Redisson 工具
│
├── src/main/resources/
│   └── application.yml        # Redis 配置
│
└── pom.xml
```

### 使用示例

#### RedisTemplate 使用

```java
@Service
public class ArticleService {
    
    @Resource
  private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 缓存文章
     */
  public void cacheArticle(Long articleId, ArticleVO article) {
        String key = "article:" + articleId;
        redisTemplate.opsForValue().set(key, article, 30, TimeUnit.MINUTES);
    }
    
    /**
     * 获取缓存的文章
     */
  public ArticleVO getCachedArticle(Long articleId) {
        String key = "article:" + articleId;
        return (ArticleVO) redisTemplate.opsForValue().get(key);
    }
    
    /**
     * 删除缓存
     */
  public void deleteCachedArticle(Long articleId) {
        String key = "article:" + articleId;
        redisTemplate.delete(key);
    }
    
    /**
     * 增加浏览量
     */
  public void incrementViewCount(Long articleId) {
        String key = "article:view:" + articleId;
        redisTemplate.opsForValue().increment(key);
    }
}
```

#### Redisson 分布式锁

```java
@Service
public class OrderService {
    
    @Resource
  private RLock lock;
    
  public void createOrder(Order order) {
        // 加锁
       lock.lock("order:" + order.getUserId());
        
        try {
            // 业务逻辑
            // ...
        } finally {
           lock.unlock();
        }
    }
}
```

---

## 4. xcz-common-datasource - 数据源配置模块

### 功能特性

- 🔄 **动态数据源** - 多数据源切换
- 📊 **读写分离** - 主从库分离
- 🗄️ **Druid 连接池** - 高性能连接池
- 📝 **MyBatis Plus** - ORM 框架集成

### 目录结构

```
xcz-common-datasource/
├── src/main/java/com/xcz/common/database/
│   ├── config/                  # 配置类
│   │   └── DataSourceConfig.java # 数据源配置
│   └── extend/                  # 扩展类
│       └── DynamicDataSource.java # 动态数据源
│
├── src/main/resources/
│   └── application.yml        # 数据源配置
│
└── pom.xml
```

### 使用示例

#### 多数据源配置

```yaml
spring:
  datasource:
    dynamic:
     primary: master
      strict: false
      datasource:
        master:
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://localhost:3306/blog_master?useUnicode=true&characterEncoding=utf8
          username: root
         password: root
        slave_1:
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://localhost:3306/blog_slave_1?useUnicode=true&characterEncoding=utf8
          username: root
         password: root
        slave_2:
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://localhost:3306/blog_slave_2?useUnicode=true&characterEncoding=utf8
          username: root
         password: root
```

#### 数据源切换

```java
@Service
public class UserService {
    
    /**
     * 查询操作（自动走从库）
     */
    @DS("slave_1")
  public List<User> queryUsers() {
        return userMapper.selectList(null);
    }
    
    /**
     * 写入操作（自动走主库）
     */
  public void createUser(User user) {
        userMapper.insert(user);
    }
    
    /**
     * 指定数据源
     */
    @DS("master")
  public void updateUser(User user) {
        userMapper.updateById(user);
    }
}
```

---

## 5. xcz-common-id - ID 生成器模块

### 功能特性

- ❄️ **雪花算法** - 分布式 ID 生成
- 🔢 **用户 ID 生成** - 专用用户 ID 生成器
- 🗄️ **数据库存储** - ID 持久化

### 目录结构

```
xcz-common-id/
├── src/main/java/com/xcz/tool/id_gener/
│   ├── repository/              # 仓库层
│   │   ├── entity/              # 实体类
│   │   │   ├── IdEntity.java  # ID 实体
│   │   │   └── UserId.java    # 用户 ID 实体
│   │   └── mapper/              # Mapper
│   │       ├── IdMapper.java  # ID Mapper
│   │       └── UserIdMapper.java # 用户 ID Mapper
│   └── util/                    # 工具类
│       ├── IdGenerateManager.java # ID 生成管理器
│       └── UserIdUtil.java    # 用户 ID 工具
│
└── pom.xml
```

### 使用示例

#### 生成分布式 ID

```java
// 生成通用 ID
Long id = IdGenerateManager.nextId();

// 生成用户 ID
Long userId = UserIdUtil.generateUserId();
```

#### 雪花算法原理

```
时间戳 (41 位) + 机器 ID (10 位) + 序列号 (12 位) = 64 位 ID

- 时间戳：毫秒级时间戳，可用约 69 年
- 机器 ID: 支持 1024 个节点
- 序列号：每毫秒可生成 4096 个 ID
```

---

## 6. xcz-common-rocketMq - RocketMQ 消息队列模块

### 功能特性

- 📨 **消息发送** - 同步/异步/延迟消息
- 📥 **消息消费** - 消息监听、批量消费
- 🔄 **事务消息** - 分布式事务支持
- ⚡ **高可用** - 集群模式、故障转移

### 目录结构

```
xcz-common-rocketMq/
├── src/main/java/com/xcz/common/rocketMq/
│   ├── config/                  # 配置类
│   │   └── RocketMQConfig.java # RocketMQ 配置
│   └── utils/                   # 工具类
│       └── RocketMQUtil.java  # RocketMQ 工具
│
├── src/main/resources/
│   └── application.yml        # RocketMQ 配置
│
└── pom.xml
```

### 使用示例

#### 发送消息

```java
@Service
public class MessageService {
    
    @Resource
  private RocketMQTemplate rocketMQTemplate;
    
    /**
     * 发送同步消息
     */
  public void sendSyncMessage(String topic, String message) {
        rocketMQTemplate.convertAndSend(topic + ":tag", message);
    }
    
    /**
     * 发送异步消息
     */
  public void sendAsyncMessage(String topic, String message) {
        rocketMQTemplate.convertAndSend(topic + ":tag", message, new SendCallback() {
            @Override
          public void onSuccess(SendResult sendResult) {
                log.info("发送成功：{}", sendResult);
            }
            
            @Override
          public void onException(Throwable e) {
                log.error("发送失败：{}", e);
            }
        });
    }
    
    /**
     * 发送延迟消息
     */
  public void sendDelayMessage(String topic, String message, int delayLevel) {
        rocketMQTemplate.syncSend(topic + ":tag", 
            MessageBuilder.withPayload(message).build(), 
           1000, // timeout
            delayLevel // delayLevel
        );
    }
}
```

#### 消费消息

```java
@Component
@RocketMQMessageListener(
    topic = "article:topic",
   consumerGroup = "article:consumer:group",
    messageModel = MessageModel.CLUSTERING
)
public class ArticleConsumer implements RocketMQListener<String> {
    
    @Override
  public void onMessage(String message) {
        log.info("收到消息：{}", message);
        
        // 处理消息
        ArticleEvent event = JSON.parseObject(message, ArticleEvent.class);
        articleService.handleEvent(event);
    }
}
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 开源协议

MIT License

---

<div align="center">

**喜欢这个项目吗？请给一个 ⭐️ Star 支持！**

</div>
