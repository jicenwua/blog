# Xcz Blog User Service - 用户服务

<div align="center">

![Java](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=spring-boot)
![MyBatis Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.9-orange)
![MongoDB](https://img.shields.io/badge/MongoDB-5.2.1-green?logo=mongodb)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

</div>

## 📖 项目简介

Xcz Blog User Service 是博客系统的用户管理微服务，负责用户注册、登录、信息管理、角色权限等核心功能。采用 Spring Boot + MyBatis Plus + MongoDB 技术栈，支持高并发用户访问和分布式部署。

### ✨ 主要特性

- 🔐 **安全认证** - BCrypt 密码加密 + JWT Token
- 👤 **用户管理** - 注册、登录、信息修改
- 🎭 **角色权限** - 多角色支持（管理员、普通用户）
- 📧 **邮箱验证** - 邮箱唯一性校验
- 🚫 **账号状态** - 支持禁用/启用账号
- 📊 **数据统计** - 用户行为统计
- 🔍 **搜索过滤** - 按邮箱、名称搜索用户

## 🏗️ 项目结构

```
xcz-blog-user/
├── src/main/java/com/xcz/
│   ├── XczBlogUserApplication.java   # 启动类
│   └── user/
│       ├── controller/                # 控制器层
│       │   └── UserController.java   # 用户接口
│       ├── mapper/                    # 数据访问层
│       │   └── UserMapper.java       # 用户 Mapper
│       ├── proto/                     # 协议定义
│       │   ├── dto/                   # 数据传输对象
│       │   │   ├── UserLoginDTO.java  # 登录 DTO
│       │   │   ├── UserRegisterDTO.java # 注册 DTO
│       │   │   └── UserUpdateDTO.java # 更新 DTO
│       │   └── vo/                    # 视图对象
│       │       ├── UserLoginVO.java  # 登录 VO
│       │       └── UserInfoVO.java   # 用户信息 VO
│       └── service/                   # 业务逻辑层
│           ├── UserService.java      # 用户服务接口
│           ├── UserServiceImpl.java  # 用户服务实现
│           └── manager/               # 业务管理器
│               └── UserManager.java  # 用户管理器
│
├── src/main/resources/
│   ├── application.yml               # 应用配置
│   └── mapper/                        # MyBatis XML
│       └── UserMapper.xml            # 用户 Mapper XML
│
└── pom.xml                           # Maven 配置
```

## 🛠️ 技术栈

| 技术 | 版本 | 描述 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.2.0 | 应用框架 |
| MyBatis Plus | 3.5.9 | ORM 框架 |
| MySQL Connector | 8.0.31 | MySQL 驱动 |
| MongoDB Driver | 5.2.1 | MongoDB 驱动 |
| JJWT | 0.12.6 | JWT 令牌库 |
| Kaptcha | 2.3.3 | 验证码生成 |
| Lombok | 1.18.30 | 代码简化 |

## 🚀 快速开始

### 环境要求

- JDK: 21+
- Maven: 3.6+
- MySQL: 8.0+
- MongoDB: 6.0+
- Redis: 7.0+
- Nacos: 2.2+

### 编译打包

```bash
cd xcz-blog-user

# 清理编译
mvn clean

# 编译打包
mvn package -DskipTests
```

### 启动服务

```bash
java-jar target/xcz-blog-user-dev-1.0-SNAPSHOT.jar
```

服务端口：`8081`

## 📋 API 接口

### 1. 用户登录

**接口**：`POST/user/login`

**请求参数**：
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**响应结果**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 123456789,
    "username": "张三",
    "email": "user@example.com",
    "role": "USER"
  }
}
```

### 2. 用户注册

**接口**：`POST /user/register`

**请求参数**：
```json
{
  "username": "张三",
  "email": "user@example.com",
  "password": "password123",
  "captcha": "a1b2c3"
}
```

**响应结果**：
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

### 3. 获取用户信息

**接口**：`GET /user/info`

**请求头**：
```
Authorization: Bearer {token}
```

**响应结果**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 123456789,
    "username": "张三",
    "email": "user@example.com",
    "avatar": "http://example.com/avatar.jpg",
    "role": "USER",
    "status": "NORMAL",
    "createTime": "2024-01-01T00:00:00"
  }
}
```

### 4. 更新用户信息

**接口**：`PUT /user/info`

**请求参数**：
```json
{
  "username": "李四",
  "avatar": "http://example.com/new-avatar.jpg"
}
```

### 5. 修改密码

**接口**：`PUT /user/password`

**请求参数**：
```json
{
  "oldPassword": "password123",
  "newPassword": "newpassword456"
}
```

### 6. 管理员 - 获取用户列表

**接口**：`GET /user/admin/list`

**请求参数**：
```
page=1&size=10&email=user@example.com&username=张三
```

**响应结果**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "userId": 123456789,
        "username": "张三",
        "email": "user@example.com",
        "role": "USER",
        "status": "NORMAL",
        "createTime": "2024-01-01T00:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 7. 管理员 - 修改用户角色

**接口**：`PUT /user/admin/role`

**请求参数**：
```json
{
  "userId": 123456789,
  "role": "ADMIN"
}
```

### 8. 管理员 - 禁用/启用账号

**接口**：`PUT /user/admin/status`

**请求参数**：
```json
{
  "userId": 123456789,
  "status": "DISABLED"
}
```

## 💾 数据库设计

### 用户表 (t_user)

```sql
CREATE TABLE `t_user` (
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `password` varchar(100) NOT NULL COMMENT '密码 (BCrypt 加密)',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像 URL',
  `role` varchar(20) NOT NULL DEFAULT 'USER' COMMENT '角色 (ADMIN/USER)',
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL' COMMENT '状态 (NORMAL/DISABLED)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### MongoDB 文档

存储用户扩展信息和行为数据：

```javascript
// collection: user_profile
{
  "_id": ObjectId("..."),
  "userId": 123456789,
  "bio": "这是一个开发者",
  "website": "https://example.com",
  "location": "北京",
  "socialLinks": {
    "github": "https://github.com/xxx",
    "twitter": "https://twitter.com/xxx"
  },
  "statistics": {
    "articleCount": 10,
    "viewCount": 5000,
    "likeCount": 200
  }
}
```

## 🔧 核心代码

### Controller 示例

```java
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    
    @Resource
  private UserService userService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO dto) {
        log.info("用户登录 - email: {}", dto.getEmail());
        UserLoginVO result = userService.login(dto);
        return Result.ok(result);
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserRegisterDTO dto) {
        log.info("用户注册 - email: {}", dto.getEmail());
        userService.register(dto);
        return Result.ok();
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    @RequiresLogin
    public Result<UserInfoVO> getUserInfo() {
        Long userId = SecurityUtils.getUserId();
        UserInfoVO userInfo = userService.getUserInfo(userId);
        return Result.ok(userInfo);
    }
    
    /**
     * 管理员获取用户列表
     */
    @GetMapping("/admin/list")
    @RequiresRoles("ADMIN")
    public Result<IPage<UserVO>> getUserList(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String username
    ) {
        IPage<UserVO> userList = userService.getUserList(page, size, email, username);
        return Result.ok(userList);
    }
}
```

### Service 示例

```java
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Resource
  private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public UserLoginVO login(UserLoginDTO dto) {
        // 查询用户
        User user = lambdaQuery()
            .eq(User::getEmail, dto.getEmail())
            .one();
        
       if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证密码
       if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        // 检查账号状态
       if ("DISABLED".equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 生成 Token
        String token = JwtUtils.createToken(
            user.getUserId(), 
            user.getUsername(), 
            86400000L // 24 小时
        );
        
        // 构建返回结果
        UserLoginVO vo = new UserLoginVO();
        vo.setToken(token);
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setRole(user.getRole());
        
        return vo;
    }
    
    @Override
    public void register(UserRegisterDTO dto) {
        // 检查邮箱是否已存在
        Long count = lambdaQuery()
            .eq(User::getEmail, dto.getEmail())
            .count();
        
       if (count > 0) {
            throw new BusinessException("邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setUserId(IdGenerateManager.nextId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setRole("USER");
        user.setStatus("NORMAL");
        
        save(user);
        
        log.info("用户注册成功 - userId: {}", user.getUserId());
    }
}
```

## 🔒 安全机制

### 密码加密

使用 BCrypt 进行密码加密：

```java
// 加密密码
String hashedPassword = BCrypt.hashpw(plainPassword);

// 验证密码
if (!BCrypt.checkpw(inputPassword, hashedPassword)) {
    throw new BusinessException("密码错误");
}
```

### JWT Token

```java
// 生成 Token
String token = JwtUtils.createToken(userId, username, 86400000L);

// 验证 Token
Claims claims = JwtUtils.parseToken(token);
Long userId = Long.parseLong(claims.getSubject());
```

## 📊 缓存策略

### Redis 缓存

```java
// 缓存用户信息
@Cacheable(value = "user", key = "#userId", unless = "#result == null")
public UserInfoVO getUserInfo(Long userId) {
    User user = getById(userId);
    return convertToVO(user);
}

// 清除缓存
@CacheEvict(value = "user", key = "#userId")
public void updateUserInfo(Long userId, UserUpdateDTO dto) {
    // 更新逻辑
}
```

## 🐛 常见问题

### 1. 登录失败

检查密码是否正确，确认 BCrypt 加密一致。

### 2. Token 过期

Token 有效期为 24 小时，过期后需要重新登录。

### 3. 邮箱重复注册

确保邮箱唯一性，注册前先检查是否已注册。

## 📝 API 文档

Swagger 文档地址：
```
http://localhost:8081/doc.html
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 开源协议

MIT License

---

<div align="center">

**喜欢这个项目吗？请给一个 ⭐️ Star 支持！**

</div>
