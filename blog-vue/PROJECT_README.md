# 博客前端项目 - Vue3 + Element Plus

## 项目说明

这是一个仿照参考页面设计的博客前端项目，使用 Vue 3 和 Element Plus 构建。

## 技术栈

- **Vue 3.5.29** - 渐进式 JavaScript 框架
- **Element Plus 2.5.6** - Vue 3 组件库
- **Vue Router** - Vue.js 官方路由
- **@element-plus/icons-vue** - Element Plus 图标库

## 项目结构

```
blog-vue/
├── src/
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   ├── router/          # 路由配置
│   │   └── index.js
│   ├── views/           # 页面组件
│   │   ├── BlogHome.vue  # 首页
│   │   ├── Articles.vue  # 文章列表页
│   │   └── About.vue     # 关于页面
│   ├── App.vue          # 根组件
│   └── main.js          # 入口文件
├── package.json         # 项目依赖配置
└── vite.config.js       # Vite 配置
```

## 安装步骤

### 1. 安装依赖

在项目根目录执行:

```bash
cd blog-vue
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

启动后访问 http://localhost:5173 查看效果

### 3. 构建生产版本

```bash
npm run build
```

## 页面功能

### 首页 (/)
- ✅ 导航栏：首页、文章、关于我、搜索按钮
- ✅ 文章列表展示 (带缩略图、标签、元数据)
- ✅ 个人简介卡片
- ✅ 公告信息
- ✅ 最新文章列表
- ✅ 人生倒计时
- ❌ 不显示：音乐播放器、夜间切换、头像、标签云

### 文章页 (/articles)
- ✅ 文章列表 (支持分类筛选)
- ✅ 分类目录树
- ✅ 热门文章
- ✅ 分页功能

### 关于页 (/about)
- ✅ 个人信息展示
- ✅ 技能专长标签
- ✅ 联系方式
- ✅ 博客说明
- ✅ 统计数据

## 主要特性

1. **响应式布局** - 支持桌面端和移动端
2. **Element Plus 组件** - 使用官方组件，质量可靠
3. **路由导航** - 单页应用，流畅切换
4. **美观设计** - 粉色樱花背景，渐变效果
5. **交互友好** - 悬停效果、过渡动画

## 自定义配置

### 修改站点信息

编辑 `BlogHome.vue`, `Articles.vue`, `About.vue` 中的:
- 站点标题："个人博客"
- Logo 图片 URL
- 个人简介内容

### 修改文章数据

在各个页面组件的 `articles` ref 中修改文章数据

### 修改配色方案

在 `<style scoped>` 中修改 CSS 变量和颜色值

## 注意事项

1. 首次运行前必须执行 `npm install` 安装依赖
2. 图片资源使用的是网络 CDN，如需更换请替换相应的 URL
3. 项目使用 Vite 构建工具，支持热更新
4. 建议使用 Node.js 20.19.0 或更高版本

## 开发规范

- 使用 Vue 3 Composition API (script setup)
- 组件命名使用 PascalCase
- 样式使用 Scoped CSS
- 保持代码整洁和注释

## 后续优化建议

1. 接入真实后端 API
2. 添加 Vuex/Pinia 状态管理
3. 实现文章详情页面
4. 添加用户登录功能
5. 集成 Markdown 渲染
6. 添加评论功能
7. 优化 SEO

## 常见问题

**Q: 样式不显示？**
A: 确保已安装 Element Plus 并正确引入 CSS 文件

**Q: 图标不显示？**
A: 确保已安装 @element-plus/icons-vue 并注册图标

**Q: 路由不工作？**
A: 确保已在 main.js 中注册 router

## 联系方式

如有问题，请联系开发者。
