# Blog Vue - 前端项目

<div align="center">

![Vue](https://img.shields.io/badge/Vue-3.5.29-brightgreen?logo=vue.js)
![Vite](https://img.shields.io/badge/Vite-7.3.1-purple?logo=vite)
![Element Plus](https://img.shields.io/badge/Element%20Plus-2.5.6-blue?logo=element)
![Axios](https://img.shields.io/badge/Axios-1.13.6-yellow)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

</div>

## 📖 项目简介

Blog Vue 是基于 Vue 3 + Vite 构建的博客系统前端项目，采用 Composition API 和组件化开发模式。集成了 Element Plus UI 组件库、WangEditor 富文本编辑器、Vue Router 路由管理等核心功能，为用户提供现代化的博客浏览和管理体验。

### ✨ 主要特性

- 🎨 **现代化界面** - Element Plus 组件库 + 响应式布局
- 🚀 **快速开发** - Vite 构建工具，热更新支持
- 📝 **富文本编辑** - WangEditor 集成，支持 Markdown、代码高亮
- 🔐 **权限控制** - 基于 Token 的路由守卫
- 🌐 **RESTful API** - Axios 封装，统一请求拦截
- 📱 **响应式设计** - 适配 PC 和移动端
- 🎯 **组件化** - 高度可复用的组件库

## 🏗️ 项目结构

```
blog-vue/
├── src/
│   ├── api/                      # API 接口层
│   │   ├── article.js            # 文章相关接口
│   │   └── user.js               # 用户相关接口
│   │
│   ├── assets/                   # 静态资源
│   │   ├── base.css              # 全局样式
│   │   └── main.css              # 主样式文件
│   │
│   ├── components/               # 公共组件
│   │   ├── admin/                # 后台管理组件
│   │   │   ├── CommentManage.vue # 评论管理
│   │   │   ├── MyArticles.vue    # 我的文章
│   │   │   ├── Publish.vue       # 文章发布
│   │   │   ├── Statistics.vue    # 数据统计
│   │   │   └── UserManage.vue    # 用户管理
│   │   ├── GlobalHeader.vue      # 全局头部
│   │   └── FallingFlowers.vue    # 落花特效
│   │
│   ├── router/                   # 路由配置
│   │   └── index.js              # 路由入口
│   │
│   ├── utils/                    # 工具函数
│   │   ├── auth.js               # 认证工具
│   │   ├── imageCompress.ts      # 图片压缩
│   │   └── request.js            # Axios 封装
│   │
│   ├── views/                    # 页面视图
│   │   ├── About.vue             # 关于页面
│   │   ├── ArticleDetail.vue     # 文章详情
│   │   ├── Articles.vue          # 文章列表
│   │   ├── BlogHome.vue          # 博客首页
│   │   ├── Login.vue             # 登录页面
│   │   ├── Register.vue          # 注册页面
│   │   ├── SearchResults.vue     # 搜索结果
│   │   └── Statistics.vue        # 统计页面
│   │
│   ├── App.vue                   # 根组件
│   └── main.js                   # 入口文件
│
├── .env.development              # 开发环境配置
├── .env.production               # 生产环境配置
├── index.html                    # HTML 模板
├── package.json                  # 项目依赖
├── vite.config.js                # Vite 配置
└── README.md                     # 项目文档
```

## 🛠️ 技术栈

| 技术 | 版本 | 描述 |
|------|------|------|
| Vue | 3.5.29 | 渐进式 JavaScript 框架 (Composition API) |
| Vite | 7.3.1 | 下一代前端构建工具 |
| Element Plus | 2.5.6 | 基于 Vue 3 的组件库 |
| Vue Router | 4.5.0 | Vue 官方路由管理器 |
| Axios | 1.13.6 | HTTP 客户端库 |
| @element-plus/icons-vue | 2.3.1 | Element Plus 图标库 |
| @tinymce/tinymce-vue | 6.3.0 | TinyMCE 编辑器组件 |

## 🚀 快速开始

### 环境要求

- Node.js: ^20.19.0 || >=22.12.0
- npm: 9.0+

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
# 启动开发服务器（热更新）
npm run dev
```

访问：`http://localhost:5173`

### 生产构建

```bash
# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

### IDE 推荐

- [VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar)

### 浏览器推荐

- Chrome / Edge / Brave (推荐安装 Vue Devtools)
- Firefox

## 📋 核心功能模块

### 1. 用户模块

#### 登录/注册
- 邮箱/密码登录
- JWT Token 认证
- 自动登录（记住我）
- 用户注册

**相关文件**: 
- [`Login.vue`](src/views/Login.vue)
- [`Register.vue`](src/views/Register.vue)
- [`auth.js`](src/utils/auth.js)

### 2. 文章模块

#### 文章展示
- 文章列表（分页）
- 文章详情
- Markdown 渲染
- 代码高亮

#### 文章管理
- 发布文章
- 编辑文章
- 删除文章
- 我的文章

**相关文件**:
- [`Articles.vue`](src/views/Articles.vue)
- [`ArticleDetail.vue`](src/views/ArticleDetail.vue)
- [`Publish.vue`](src/components/admin/Publish.vue)
- [`MyArticles.vue`](src/components/admin/MyArticles.vue)
- [`article.js`](src/api/article.js)

### 3. 评论模块

- 文章评论
- 评论回复
- 评论管理

**相关文件**:
- [`CommentManage.vue`](src/components/admin/CommentManage.vue)

### 4. 搜索模块

- 全文搜索
- 关键词高亮
- 搜索结果分页

**相关文件**:
- [`SearchResults.vue`](src/views/SearchResults.vue)

### 5. 统计模块

- 访问量统计
- 点赞数统计
- 评论数统计
- 数据可视化

**相关文件**:
- [`Statistics.vue`](src/views/Statistics.vue)
- [`Statistics.vue`](src/components/admin/Statistics.vue)

### 6. 后台管理

- 用户管理（角色、权限）
- 文章管理
- 评论管理
- 数据统计

**相关文件**:
- [`UserManage.vue`](src/components/admin/UserManage.vue)

## 🔧 配置说明

### 环境变量

#### 开发环境 (`.env.development`)

```env
VITE_API_BASE_URL=http://localhost:8080
```

#### 生产环境 (`.env.production`)

```env
VITE_API_BASE_URL=https://your-domain.com
```

### Axios 配置

[`request.js`](src/utils/request.js) 封装了统一的 HTTP 请求：

```javascript
import request from './utils/request'

// GET 请求
request.get('/api/articles', { page: 1, size: 10 })

// POST 请求
request.post('/api/articles', { title, content, tags })

// PUT 请求
request.put('/api/articles/:id', data)

// DELETE 请求
request.delete('/api/articles/:id')
```

### 路由配置

[`router/index.js`](src/router/index.js) 定义了所有路由：

```javascript
{
  path: '/',
  name: 'home',
  component: BlogHome
},
{
  path: '/articles',
  name: 'articles',
  component: Articles
},
{
  path: '/article/:id',
  name: 'article-detail',
  component: ArticleDetail
},
{
  path: '/login',
  name: 'login',
  component: Login
}
```

## 📁 API 接口

### 用户接口 (`user.js`)

```javascript
// 用户登录
login(email, password)

// 用户注册
register(username, email, password)

// 获取用户信息
getUserInfo()

// 退出登录
logout()
```

### 文章接口 (`article.js`)

```javascript
// 获取文章列表
getArticleList(page, size)

// 获取文章详情
getArticleDetail(id)

// 发布文章
publishArticle(data)

// 更新文章
updateArticle(id, data)

// 删除文章
deleteArticle(id)

// 搜索文章
searchArticles(keyword, page, size)

// 获取统计数据
getStatistics()
```

## 🎨 组件使用示例

### 全局头部

```vue
<template>
  <GlobalHeader />
</template>

<script setup>
import GlobalHeader from '@/components/GlobalHeader.vue'
</script>
```

### 富文本编辑器

```vue
<template>
  <Publish />
</template>

<script setup>
import Publish from '@/components/admin/Publish.vue'
</script>
```

### 落花特效

```vue
<template>
  <FallingFlowers />
</template>

<script setup>
import FallingFlowers from '@/components/FallingFlowers.vue'
</script>
```

## 🔒 认证与授权

### Token 管理

[`auth.js`](src/utils/auth.js) 提供了统一的 Token 管理：

```javascript
import { getToken, setToken, removeToken } from '@/utils/auth'

// 存储 Token
setToken(token)

// 获取 Token
const token = getToken()

// 移除 Token
removeToken()
```

### 路由守卫

```javascript
router.beforeEach((to, from, next) => {
  const token = getToken()
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})
```

## 📊 性能优化

### 图片压缩

[`imageCompress.ts`](src/utils/imageCompress.ts) 实现了图片压缩上传：

```typescript
import { compressImage } from '@/utils/imageCompress'

const compressedFile = await compressImage(file, 0.7)
```

### 懒加载

```vue
<template>
  <img v-lazy="imageUrl" alt="article image" />
</template>
```

### 组件按需引入

```javascript
// 按需引入 Element Plus 组件
import { ElButton, ElTable } from 'element-plus'

app.use(ElButton)
app.use(ElTable)
```

## 🐛 常见问题

### 1. 跨域问题

开发环境下，Vite 已配置代理解决跨域：

```javascript
// vite.config.js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

### 2. 富文本编辑器不显示

确保正确引入 WangEditor：

```javascript
import { Editor } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'
```

### 3. 路由跳转空白

检查路由守卫和 Token 状态：

```javascript
console.log('Token:', getToken())
console.log('Route:', to.path)
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 开源协议

MIT License

---

<div align="center">

**喜欢这个项目吗？请给一个 ⭐️ Star 支持！**

</div>
