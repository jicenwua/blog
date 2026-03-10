import { createRouter, createWebHistory } from 'vue-router'
import BlogHome from '../views/BlogHome.vue'
import { getToken, getUserInfo, hasRole } from '@/utils/auth'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: BlogHome
  },
  {
    path: '/articles',
    name: 'Articles',
    component: () => import('../views/Articles.vue')
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('../views/ArticleDetail.vue')
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/About.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('../views/Statistics.vue'),
    meta: { requiresAdmin: true } // 需要管理员权限
  },
  {
    path: '/my-articles',
    name: 'MyArticles',
    component: () => import('../components/admin/MyArticles.vue'),
    meta: { requiresAdmin: true }
  },
  {
    path: '/admin/publish',
    name: 'Publish',
    component: () => import('../components/admin/Publish.vue'),
    meta: { requiresAdmin: true }
  },
  {
    path: '/search',
    name: 'SearchResults',
    component: () => import('../views/SearchResults.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  
  // 只需要检查是否有 token，不再每次刷新都调用后端验证
  // Token 的有效性由 Gateway 的全局过滤器负责验证
  if (to.meta.requiresAdmin) {
    // 访问需要管理员权限的页面
    if (!token) {
      // 没有 token，直接跳转到登录页
      next('/login')
      return
    }
    
    const userInfo = getUserInfo()
    if (!userInfo || !hasRole('admin')) {
      // 不是管理员，重定向到首页
      next('/')
      return
    }
    // 是管理员，放行
    next()
  } else {
    // 普通页面，有 token 就直接放行
    next()
  }
})

export default router
