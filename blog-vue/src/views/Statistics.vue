<template>
  <div class="admin-layout">
    <!-- 左侧导航栏 -->
    <el-aside class="admin-aside">
      <div class="logo-section">
        <img src="/src/components/icons/shala.png" alt="Logo" class="logo" />
        <span class="site-title">管理后台</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="admin-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="1">
          <el-icon><DataAnalysis /></el-icon>
          <span>统计</span>
        </el-menu-item>
        
        <el-menu-item index="2">
          <el-icon><Document /></el-icon>
          <span>文章</span>
        </el-menu-item>
        
        <el-menu-item index="3">
          <el-icon><ChatDotRound /></el-icon>
          <span>评论</span>
        </el-menu-item>
        
        <el-menu-item index="4">
          <el-icon><User /></el-icon>
          <span>用户</span>
        </el-menu-item>
      </el-menu>
      
      <div class="back-home">
        <el-button text @click="$router.push('/')">
          <el-icon><ArrowLeft /></el-icon>
          返回首页
        </el-button>
      </div>
    </el-aside>

    <!-- 主内容区 -->
    <el-main class="admin-main">
      <!-- 统计页面 -->
      <Statistics v-if="activeMenu === '1'" />
      
      <!-- 文章管理页面 -->
      <MyArticles v-if="activeMenu === '2'" />
      
      <!-- 评论管理页面 -->
      <CommentManage v-if="activeMenu === '3'" />
      
      <!-- 用户管理页面 -->
      <UserManage v-if="activeMenu === '4'" />
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { DataAnalysis, Document, User, ChatDotRound, ArrowLeft } from '@element-plus/icons-vue'
import Statistics from '../components/admin/Statistics.vue'
import MyArticles from '../components/admin/MyArticles.vue'
import UserManage from '../components/admin/UserManage.vue'
import CommentManage from '../components/admin/CommentManage.vue'

const route = useRoute()
const activeMenu = ref('1')

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

// 页面加载时检查是否有菜单参数
onMounted(() => {
  const menuIndex = route.query.menu
  if (menuIndex) {
    activeMenu.value = menuIndex
  }
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background-color: #f0f2f5;
  background-image:
    linear-gradient(to right, rgba(0, 0, 0, 0.05) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(0, 0, 0, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  background-attachment: fixed;
}

.admin-aside {
  width: 220px;
  background: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.logo-section {
  height: 60px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 20px;
  border-bottom: 1px solid #e6e6e6;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.site-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.admin-menu {
  flex: 1;
  border-right: none;
  padding: 10px 0;
}

.admin-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  margin: 5px 10px;
  border-radius: 6px;
  color: #606266;
}

.admin-menu .el-menu-item:hover {
  background-color: #ecf5ff;
  color: #409EFF;
}

.admin-menu .el-menu-item.is-active {
  background-color: #ecf5ff;
  color: #409EFF;
}

.back-home {
  padding: 20px;
  border-top: 1px solid #e6e6e6;
}

.back-home .el-button {
  width: 100%;
}

.admin-main {
  flex: 1;
  padding: 0;
  overflow: hidden;
  background: transparent;
}
</style>
