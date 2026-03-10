# Xcz Blog Gateway - 网关服务

<div align="center">

![Java](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=spring-boot)
![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-2023.0.0-green?logo=spring-cloud)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

</div>

## 📖 项目简介

Xcz Blog Gateway 是博客系统的统一入口网关，基于 Spring Cloud Gateway 构建。负责请求路由、身份认证、限流控制、安全防护等核心功能，为后端微服务提供统一的流量管理和安全保护。

### ✨ 主要特性

- 🚦 **动态路由** - 基于 Nacos 的服务发现与路由
- 🔐 **全局认证** - JWT Token 统一验证
- 🛡️ **安全防护** - XSS 过滤、SQL 注入防护
- ⚡ **限流控制** - 基于 Redis 的接口限流
- 📝 **日志记录** - 请求日志统一收集
- 🔄 **跨域支持** - CORS 配置
- 🔍 **健康检查** - 服务状态监控

## 🏗️ 项目结构

```
xcz-gateway/
├── src/main/java/com/xcz/
│   ├── DimpleGatewayApplication.java    # 启动类
│   └── gateway/
│       ├── config/                      # 网关配置
│       │   ├── CorsConfig.java          # 跨域配置
│       │   ├── GlobalFilterConfig.java  # 全局过滤器配置
│       │   └── RouteConfig.java         # 路由配置
│       ├── filter/                      # 自定义过滤器
│       │   ├── AuthGlobalFilter.java    # 认证过滤器
│       │   ├── LimitingFilter.java      # 限流过滤器
│       │   ├── LogGlobalFilter.java     # 日志过滤器
│       │   └── XssGlobalFilter.java     # XSS 过滤
│       ├── handler/                     # 异常处理
│       │   └── GlobalErrorWebExceptionHandler.java
│       └── proto/                       # 协议定义
│           └── Result.java              # 统一响应
│
└── src/main/resources/
    └── application.yml                  # 配置文件
```

## 🛠️ 技术栈

| 技术 | 版本 | 描述 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.2.0 | 应用框架 |
| Spring Cloud Gateway | 2023.0.0 | 网关框架 |
| Spring Cloud Alibaba | 2023.0.0.0-RC1 | 服务注册发现 |
| Redisson | 3.50.0 | Redis 客户端 |
| JJWT | 0.12.6 | JWT 令牌库 |

## 🚀 快速开始

### 环境要求

- JDK: 21+
- Maven: 3.6+
- Redis: 7.0+
- Nacos: 2.2+

### 编译打包

```bash
cd xcz-gateway

# 清理编译
mvn clean

# 编译打包
mvn package -DskipTests
```

### 启动网关

```bash
java-jar target/xcz-gateway-dev-1.0-SNAPSHOT.jar
```

访问：`http://localhost:8080`

## ⚙️ 配置说明

### 基础配置 (`application.yml`)

```yaml
server:
  port:8080

spring:
  application:
   name: blog-gateway-server
  profiles:
   active: @profiles.active@
  cloud:
  nacos:
      discovery:
        server-addr: 127.0.0.1:8848
       username: nacos
      password: nacos
     config:
        server-addr: 127.0.0.1:8848
        file-extension: yaml
       namespace: public
        group: BLOG_GROUP
```

### 路由配置

网关自动从 Nacos 获取服务列表并配置路由：

```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      routes:
        - id: blog-user
          uri: lb://blog-user
         predicates:
            - Path=/user/**
        - id: blog-article
          uri: lb://blog-article
         predicates:
            - Path=/article/**
```

## 🔐 核心功能

### 1. 全局认证过滤器

验证 JWT Token，拦截未授权请求：

```java
@Component
public class AuthGlobalFilter implements GlobalFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = extractToken(exchange.getRequest());
        
        if (StringUtils.isEmpty(token)) {
            return unauthorized(exchange, "Token 为空");
        }
        
        try {
            // 验证 Token
            Claims claims = JwtUtils.parseToken(token);
            
            // 将用户信息传递到下游服务
            ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-User-Id", claims.getSubject())
                .header("X-Username", claims.get("username", String.class))
                .build();
            
            return chain.filter(exchange.mutate().request(request).build());
        } catch (Exception e) {
            return unauthorized(exchange, "Token 无效或已过期");
        }
    }
}
```

### 2. XSS 防护过滤器

过滤恶意脚本，防止 XSS 攻击：

```java
@Component
public class XssGlobalFilter implements GlobalFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 只对 POST/PUT 请求进行过滤
        if (HttpMethod.POST.equals(request.getMethod()) || 
            HttpMethod.PUT.equals(request.getMethod())) {
            
            String body = readBody(request);
            String cleanedBody = XssUtils.clean(body);
            
            // 替换请求体
            ServerHttpRequest newRequest = mutateRequest(request, cleanedBody);
            return chain.filter(exchange.mutate().request(newRequest).build());
        }
        
        return chain.filter(exchange);
    }
}
```

### 3. 限流过滤器

基于 Redis 实现接口限流：

```java
@Component
public class LimitingFilter implements GlobalFilter {
    
    @Resource
   private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String key = "rate_limit:" + exchange.getRequest().getPath().value();
        String ip = getClientIp(exchange.getRequest());
        
        Long count = redisTemplate.opsForValue().increment(key);
        
        if (count == 1) {
            // 第一次访问，设置过期时间
            redisTemplate.expire(key, 60, TimeUnit.SECONDS);
        }
        
        if (count > 100) {
            // 超过限制
            return tooManyRequests(exchange);
        }
        
        return chain.filter(exchange);
    }
}
```

### 4. 日志过滤器

记录请求日志，用于审计和排查问题：

```java
@Component
@Slf4j
public class LogGlobalFilter implements GlobalFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求开始 - Method: {}, URI: {}, IP: {}", 
            request.getMethod(), 
            request.getURI(), 
            getClientIp(request));
        
        return chain.filter(exchange).doOnSuccess(v -> {
            long endTime = System.currentTimeMillis();
            log.info("请求结束 - 耗时：{}ms, 状态码：{}", 
                endTime - startTime, 
                exchange.getResponse().getStatusCode());
        });
    }
}
```

### 5. 跨域配置

支持跨域请求：

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsWebFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        CorsConfiguration config = new CorsConfiguration();
       config.setAllowCredentials(true);
       config.addAllowedOriginPattern("*");
       config.addAllowedHeader("*");
       config.addAllowedMethod("*");
       config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
}
```

## 🔍 路由规则

### 默认路由

网关自动将请求路由到对应的微服务：

| 请求路径 | 目标服务 | 服务端口 |
|---------|---------|---------|
| `/user/**` | blog-user | 8081 |
| `/article/**` | blog-article | 8082 |
| `/api/**` | 根据路由规则 | - |

### 动态路由

通过 Nacos 配置中心动态调整路由：

```yaml
# Nacos 配置中心 - blog-gateway-server.yaml
spring:
  cloud:
    gateway:
      routes:
        - id: blog-user
          uri: lb://blog-user
         predicates:
            - Path=/api/user/**
          filters:
            - StripPrefix=1
```

## 🛡️ 安全机制

### JWT Token 验证

```java
public class JwtUtils {
    
    // 生成 Token
    public static String createToken(Long userId, String username, long expiration) {
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("username", username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    // 验证 Token
    public static Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // 解析 Token
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
```

### XSS 过滤

```java
public class XssUtils {
    
   private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script.*?>.*?</script>", Pattern.CASE_INSENSITIVE);
   private static final Pattern ON_EVENT_PATTERN = Pattern.compile("on\\w+\\s*=\\s*['\"].*?['\"]", Pattern.CASE_INSENSITIVE);
    
    public static String clean(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }
        
        // 移除脚本标签
        input = SCRIPT_PATTERN.matcher(input).replaceAll("");
        
        // 移除事件处理器
        input = ON_EVENT_PATTERN.matcher(input).replaceAll("");
        
        // HTML 转义
        input = StringEscapeUtils.escapeHtml4(input);
        
        return input;
    }
}
```

## 📊 监控与日志

### 健康检查端点

```bash
# 检查网关状态
curl http://localhost:8080/actuator/health

# 查看路由信息
curl http://localhost:8080/actuator/gateway/routes
```

### 日志格式

```
2024-01-01 10:00:00 INFO  - 请求开始 - Method: GET, URI: /article/list, IP: 192.168.1.100
2024-01-01 10:00:01 INFO  - 请求结束 - 耗时：150ms, 状态码：200
```

## 🐛 常见问题

### 1. 服务无法注册到 Nacos

检查 Nacos 配置：
```yaml
spring:
  cloud:
  nacos:
      discovery:
        server-addr: 127.0.0.1:8848
       username: nacos
      password: nacos
```

### 2. 跨域请求失败

确认 CORS 配置已启用：
```java
@Bean
public CorsWebFilter corsFilter() {
    // ... 配置跨域
}
```

### 3. Token 验证失败

检查 JWT 密钥配置：
```yaml
jwt:
  secret: your-secret-key-here
  expiration: 86400000
```

## 📝 API 文档

网关本身不提供业务接口，所有请求都会转发到对应的微服务。

API 文档地址：
```
http://localhost:8080/doc.html
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 开源协议

MIT License

---

<div align="center">

**喜欢这个项目吗？请给一个 ⭐️ Star 支持！**

</div>
