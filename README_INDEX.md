# 📚 README 导航索引

欢迎使用 Xcz Blog 博客系统！本文档将帮助您快速找到所需的 README 文档。

## 🗂️ 文档目录

### 全局文档

| 文档 | 路径 | 描述 |
|------|------|------|
| **主 README** | [`/README.md`](../README.md) | 项目整体介绍、架构、技术栈 |

---

### 前端项目

| 文档 | 路径 | 描述 |
|------|------|------|
| **Blog Vue** | [`/blog-vue/README_GITHUB.md`](../blog-vue/README_GITHUB.md) | Vue3 前端项目详细说明 |

**包含内容**：
- 项目结构
- 技术栈（Vue 3 + Vite + Element Plus）
- 功能模块（登录、文章、评论、管理等）
- API 接口调用
- 配置说明
- 常见问题

---

### 后端项目

#### 整体文档

| 文档 | 路径 | 描述 |
|------|------|------|
| **后端总览** | [`/xcz-blog/README.md`](../xcz-blog/README.md) | 后端整体架构、模块划分 |

#### 微服务模块

| 文档 | 路径 | 描述 | 端口 |
|------|------|------|------|
| **Gateway** | [`/xcz-blog/xcz-gateway/README.md`](../xcz-blog/xcz-gateway/README.md) | 网关服务（路由、认证、限流） | 8080 |
| **User Service** | [`/xcz-blog/xcz-blog-user/README.md`](../xcz-blog/xcz-blog-user/README.md) | 用户服务（注册、登录、权限） | 8081 |
| **Article Service** | [`/xcz-blog/xcz-blog-article/README.md`](../xcz-blog/xcz-blog-article/README.md) | 文章服务（发布、管理、搜索） | 8082 |

#### 公共模块

| 文档 | 路径 | 描述 |
|------|------|------|
| **公共模块合集** | [`/xcz-blog/README_COMMON.md`](../xcz-blog/README_COMMON.md) | 所有公共模块的详细说明 |

**包含子模块**：
- `xcz-common-core` - 核心工具包
- `xcz-common-security` - 安全认证模块
- `xcz-common-cache` - 缓存模块
- `xcz-common-datasource` - 数据源配置
- `xcz-common-id` - ID 生成器
- `xcz-common-rocketMq` - RocketMQ 消息队列

---

## 🎯 快速导航

### 按角色分类

#### 👨‍💻 前端开发者

推荐阅读顺序：
1. [主 README](../README.md) - 了解整体架构
2. [Blog Vue README](../blog-vue/README_GITHUB.md) - 前端项目详情
3. [API 接口文档](http://localhost:8080/doc.html) - 查看接口定义

#### ☕ 后端开发者

推荐阅读顺序：
1. [主 README](../README.md) - 了解整体架构
2. [后端总览](../xcz-blog/README.md) - 后端架构设计
3. [公共模块](../xcz-blog/README_COMMON.md) - 工具类使用
4. 各微服务 README - 具体业务实现

#### 🚀 运维人员

推荐阅读顺序：
1. [主 README](../README.md) - 环境要求
2. [后端总览](../xcz-blog/README.md) - 部署配置
3. 各服务配置文件 - 实际部署参数

#### 📊 产品经理

推荐阅读顺序：
1. [主 README](../README.md) - 产品功能
2. [前端 README](../blog-vue/README_GITHUB.md) - 界面功能
3. [后端总览](../xcz-blog/README.md) - 技术架构

---

## 📖 阅读建议

### 新手入门

1. **先读主 README** - 了解项目整体情况
2. **查看架构图** - 理解系统设计
3. **快速开始** - 本地运行项目
4. **深入模块** - 根据需求查看具体模块

### 问题排查

1. **查看对应模块 README** - 寻找常见问题
2. **检查配置文件** - 确认配置正确
3. **查看日志** - 定位具体问题
4. **提交 Issue** - 寻求社区帮助

---

## 🔗 外部链接

### 官方文档

- [Vue.js](https://vuejs.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis Plus](https://baomidou.com/)
- [Element Plus](https://element-plus.org/)
- [Nacos](https://nacos.io/)

### API 文档

- Gateway Swagger: http://localhost:8080/doc.html
- User Service Swagger: http://localhost:8081/doc.html
- Article Service Swagger: http://localhost:8082/doc.html

---

## 📝 更新日志

| 日期 | 文档 | 更新内容 |
|------|------|----------|
| 2026-03-11 | 全部 README | 初始版本创建 |

---

## 🤝 贡献指南

如果您发现文档有问题或需要补充，欢迎：

1. 提交 Issue
2. 提交 Pull Request
3. 联系项目维护者

---

<div align="center">

**祝您使用愉快！**

如有问题，请查看对应的 README 文档或提交 Issue。

</div>
