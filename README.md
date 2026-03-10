# Xcz Blog- 全栈博客系统

<div align="center">

![Java](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=spring-boot)
![Vue](https://img.shields.io/badge/Vue-3.5.29-brightgreen?logo=vue.js)
![Vite](https://img.shields.io/badge/Vite-7.3.1-purple?logo=vite)
![MyBatis Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.9-orange)
![MongoDB](https://img.shields.io/badge/MongoDB-5.2.1-green?logo=mongodb)
![Redis](https://img.shields.io/badge/Redis-3.50.0-red?logo=redis)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

</div>

## 📖 项目简介

Xcz Blog 是一个基于前后端分离架构的现代化博客系统，采用微服务模块化设计。系统支持文章的发布、管理、评论、统计等功能，同时提供完善的用户管理和权限控制机制。

### ✨ 主要特性

- 🎨 **现代化 UI** - 基于 Vue 3 + Element Plus 构建的响应式界面
- 📝 **富文本编辑** - 支持 Markdown、代码高亮、图文混排
- 🔐 **安全认证** - JWT Token + Spring Security 双重保障
- 🚀 **高性能缓存** - Redis + Redisson 多级缓存策略
- 📊 **数据统计** - 实时文章统计、用户行为分析
- 🔍 **全文搜索** - Elasticsearch 驱动的高效检索
- 📦 **微服务架构** - Spring Cloud Alibaba + Nacos 服务治理
- 🌐 **网关路由** - 统一网关入口，支持限流、鉴权、XSS 防护

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                    前端层 (Blog-Vue)                      │
│  Vue 3 + Vite + Element Plus + Vue Router               │
└──────────────────┬──────────────────────────────────────┘
                   │ HTTP/REST API
┌──────────────────▼──────────────────────────────────────┐
│                  网关层 (Gateway)                        │
│  Spring Cloud Gateway + XSS Filter + Rate Limiter       │
└──────────────────┬──────────────────────────────────────┘
                   │
        ┌──────────┴──────────┐
        ▼                     ▼
┌──────────────┐      ┌──────────────┐
│  用户服务     │      │  文章服务     │
│  (User)      │      │  (Article)   │
│  Spring Boot │      │  Spring Boot │
└──────┬───────┘      └──────┬───────┘
       │                     │
       ▼                     ▼
┌──────────────────────────────────────┐
│         数据存储与缓存层              │
│  MySQL + MongoDB + Redis + ES        │
└──────────────────────────────────────┘
```

## 📁 项目结构

```
blog/
├── blog-vue/                          # 前端项目 (Vue 3)
│   ├── src/
│   │   ├── api/                       # API 接口
│   │   ├── components/                # 组件库
│   │   ├── router/                    # 路由配置
│   │   ├── utils/                     # 工具函数
│   │   └── views/                     # 页面视图
│   ├── package.json
│   └── vite.config.js
│
├── xcz-blog/                          # 后端项目 (Spring Boot)
│   ├── xcz-gateway/                   # 网关服务
│   ├── xcz-blog-user/                 # 用户服务模块
│   ├── xcz-blog-article/              # 文章服务模块
│   ├── xcz-common-core/               # 核心工具包
│   ├── xcz-common-security/           # 安全认证模块
│   ├── xcz-common-cache/              # 缓存模块
│   ├── xcz-common-datasource/         # 数据源配置
│   ├── xcz-common-id/                 # ID 生成器
│   └── xcz-common-rocketMq/           # RocketMQ 消息队列
│
└── README.md
```

## 🛠️ 技术栈

### 前端技术栈

| 技术 | 版本 | 描述 |
|------|------|------|
| Vue | 3.5.29 | 渐进式 JavaScript 框架 |
| Vite | 7.3.1 | 下一代前端构建工具 |
| Element Plus | 2.5.6 | Vue 3 组件库 |
| Vue Router | 4.5.0 | Vue 官方路由 |
| Axios | 1.13.6 | HTTP 请求库 |
| @wangeditor/editor | 5.1.23 | 富文本编辑器 |

### 后端技术栈

| 技术 | 版本 | 描述 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.2.0 | 应用框架 |
| Spring Cloud | 2023.0.0 | 微服务框架 |
| MyBatis Plus | 3.5.9 | ORM 框架 |
| MongoDB | 5.2.1 | NoSQL 数据库 |
| Redis | 3.50.0 | 缓存数据库 |
| Elasticsearch | 8.11.1 | 全文搜索引擎 |
| Nacos | - | 服务注册与配置中心 |
| RocketMQ | - | 消息队列 |

## 🚀 快速开始

### 环境要求

- Node.js: ^20.19.0 || >=22.12.0
- JDK: 21+
- Maven: 3.6+
- MySQL: 8.0+
- MongoDB: 6.0+
- Redis: 7.0+
- Nacos: 2.2+

### 前端启动

```bash
cd blog-vue

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 生产环境构建
npm run build
```

访问：`http://localhost:5173`

### 后端启动

```bash
cd xcz-blog

# 编译打包
mvn clean package -DskipTests

# 启动网关服务
java -jar xcz-gateway/target/xcz-gateway-dev-1.0-SNAPSHOT.jar

# 启动用户服务
java -jar xcz-blog-user/target/xcz-blog-user-dev-1.0-SNAPSHOT.jar

# 启动文章服务
java -jar xcz-blog-article/target/xcz-blog-article-dev-1.0-SNAPSHOT.jar
```

服务端口：
- Gateway: 8080
- User Service: 8081
- Article Service: 8082

## 📸 功能截图

### 首页展示
![Home Page](./docs/images/home.png)

### 文章详情
![Article Detail](./docs/images/article.png)

### 后台管理
![Admin Dashboard](./docs/images/admin.png)

### 文章发布
![Publish](./docs/images/publish.png)

## 🎯 核心功能

### 用户模块
- [x] 用户注册/登录
- [x] JWT 身份认证
- [x] 角色权限管理
- [x] 用户信息管理

### 文章模块
- [x] 文章发布/编辑
- [x] 富文本编辑器
- [x] Markdown 支持
- [x] 文章分类/标签
- [x] 文章收藏/点赞

### 评论模块
- [x] 文章评论
- [x] 评论回复
- [x] 评论管理

### 统计模块
- [x] 访问量统计
- [x] 点赞数统计
- [x] 评论数统计
- [x] 数据可视化

### 搜索模块
- [x] 全文检索
- [x] 关键词高亮
- [x] 搜索结果分页

## ⚙️ 配置文件

### 开发环境配置

修改 `blog-vue/.env.development`:
```env
VITE_API_BASE_URL=http://localhost:8080
```

修改 `xcz-blog/xcz-gateway/src/main/resources/application.yml`:
```yaml
spring:
  profiles:
    active: dev
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
```

## 📝 API 文档

后端服务集成了 Swagger API 文档，访问地址：
```
http://localhost:8080/doc.html
```

## 🔒 安全机制

- JWT Token 认证
- Spring Security 权限控制
- XSS 攻击防护
- SQL 注入防护
- 接口限流
- 敏感数据加密

## 📊 性能优化

- Redis 缓存策略
- MongoDB 存储大文本
- Elasticsearch 全文检索
- 图片压缩上传
- 前端懒加载
- CDN 静态资源

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 开源协议

本项目采用 MIT 协议开源 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 👥 开发团队

- Developer: Xcz
- Email: [your-email@example.com]
- GitHub: [your-github]

## 🙏 致谢

感谢以下开源项目：

- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis Plus](https://baomidou.com/)
- [WangEditor](https://www.wangeditor.com/)

---

<div align="center">

如果这个项目对你有帮助，请给一个 ⭐️ Star 支持！

</div>
