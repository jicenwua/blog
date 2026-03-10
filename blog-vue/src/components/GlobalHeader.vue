<template>
  <el-header class="header">
    <div class="header-content">
      <div class="logo-section">
        <img src="/src/components/icons/shala.png" alt="Logo" class="logo" />
        <span class="site-title">个人博客</span>
      </div>

      <el-menu
        mode="horizontal"
        :ellipsis="false"
        class="nav-menu"
        :default-active="activeMenu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="1">
          <el-icon><img src="/src/components/icons/bingqilin.png" alt="Logo" class="logo" /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="2">
          <el-icon><img src="/src/components/icons/shutiao.png" alt="Logo" class="logo" /></el-icon>
          <span>文章</span>
        </el-menu-item>
<!--        <el-menu-item index="3">-->
<!--          <el-icon><img src="/src/components/icons/jitui.png" alt="Logo" class="logo" /></el-icon>-->
<!--          <span>关于我</span>-->
<!--        </el-menu-item>-->
        <!-- 管理员专属菜单 -->
        <el-menu-item index="5" v-if="isAdmin">
          <el-icon><el-icon><img src="/src/components/icons/dangao.png" alt="Logo" class="logo" /></el-icon></el-icon>
          <span>博客统计</span>
        </el-menu-item>
      </el-menu>

      <div class="search-section">
        <el-input
          v-model="searchQuery"
          placeholder="搜索文章..."
          class="search-input"
          :prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
        />
      </div>

      <div class="auth-section" v-if="!isLoggedIn">
        <el-button type="primary" text class="auth-btn" @click="$router.push('/login')">登录</el-button>
        <el-divider direction="vertical" />
        <el-button type="primary" text class="auth-btn" @click="$router.push('/register')">注册</el-button>
      </div>

      <!-- 已登录显示用户信息 -->
      <div class="user-section" v-else>
        <el-dropdown trigger="click" class="user-dropdown">
          <div class="user-info">
            <el-avatar :size="36" class="user-avatar">
              {{ userInfo.userName ? userInfo.userName.charAt(0).toUpperCase() : 'U' }}
            </el-avatar>
            <span class="user-name">{{ userInfo.userName || '用户' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  ArrowDown,
  User,
  SwitchButton,
  DataAnalysis
} from '@element-plus/icons-vue'
import { getUserInfo, isLoggedIn as checkLoggedIn, hasRole, logout as userLogout } from '@/utils/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const searchQuery = ref('')

// 用户状态
const isLoggedIn = ref(false)
const isAdmin = ref(false)
const userInfo = ref(null)

// 根据当前路由计算激活的菜单项
const activeMenu = computed(() => {
  const pathMap = {
    '/': '1',
    '/articles': '2',
    '/about': '3',
    '/my-articles': '4',
    '/statistics': '5'
  }
  return pathMap[route.path] || '1'
})

// 处理菜单选择
const handleMenuSelect = (index) => {
  const pathMap = {
    '1': '/',
    '2': '/articles',
    '3': '/about',
    '4': '/my-articles',
    '5': '/statistics'
  }
  if (pathMap[index]) {
    router.push(pathMap[index])
  }
}

// 处理搜索
const handleSearch = () => {
  const keyword = searchQuery.value.trim()
  if (keyword) {
    // 如果已经在搜索结果页面，且关键词相同，强制刷新
    if (route.path === '/search' && route.query.keyword === keyword) {
      // 路由参数相同，watch 不会触发，需要手动触发或强制刷新
      // 方案：先跳转到其他页面再跳回来，或者使用 key 强制重新渲染
      // 这里采用简单的方式：直接调用搜索 API 的页面刷新
      window.location.reload()
    } else {
      // 跳转到搜索结果页面
      router.push({
        path: '/search',
        query: {
          keyword: keyword
        }
      })
    }
  }
}

// 检查登录状态
const checkLoginStatus = () => {
  isLoggedIn.value = checkLoggedIn()
  if (isLoggedIn.value) {
    const info = getUserInfo()
    userInfo.value = info
    // 检查是否为管理员
    isAdmin.value = hasRole('admin')
  }
}

// 处理退出登录
const handleLogout = async () => {
  try {
    await userLogout()
    isLoggedIn.value = false
    userInfo.value = null
    isAdmin.value = false
    ElMessage.success('已退出登录')
    router.push('/')
  } catch (error) {
    isLoggedIn.value = false
    userInfo.value = null
    isAdmin.value = false
    ElMessage.success('已退出登录')
    router.push('/')
  }
}

onMounted(() => {
  checkLoginStatus()
})
</script>

<style scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 20px;
  gap: 20px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.site-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.nav-menu {
  border: none;
  background: transparent;
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-menu .el-menu-item {
  border: none;
  color: #606266;
  font-size: 15px;
  cursor: pointer;
}

.nav-menu .el-menu-item:hover {
  background-color: #f5f7fa;
  color: #409EFF;
}

.nav-menu .el-menu-item.is-active {
  color: #409EFF;
}

.search-section {
  width: 250px;
}

.search-input {
  width: 100%;
}

/* 登录注册按钮 */
.auth-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.auth-btn {
  font-size: 14px;
  padding: 8px 16px;
}

.user-section {
  display: flex;
  align-items: center;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 20px;
  background: #f5f7fa;
  transition: all 0.3s;
}

.user-info:hover {
  background: #ecf5ff;
}

.user-avatar {
  background: #409EFF;
  color: #fff;
  font-weight: bold;
}

.user-name {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.el-dropdown-menu__item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
}

/* 管理员标识 */
.admin-badge {
  display: flex;
  align-items: center;
  margin-left: 10px;
}

/* 响应式 */
@media (max-width: 768px) {
  .header-content {
    flex-wrap: wrap;
    height: auto;
    padding: 10px 20px;
  }

  .nav-menu {
    width: 100%;
    order: 3;
    margin-top: 10px;
  }

  .search-section {
    width: 200px;
  }
}
</style>
