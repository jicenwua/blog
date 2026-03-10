<script setup>
import { ref, onMounted } from 'vue'
import { RouterView, useRouter } from 'vue-router'
import FallingFlowers from './components/FallingFlowers.vue'
import GlobalHeader from './components/GlobalHeader.vue'
import { initUserState, logout } from '@/utils/auth'

// 不需要落花特效的页面路径
const noFlowerPages = ref([
  // 可以在这里添加不需要落花特效的页面路径
])

// 不需要显示抬头的页面路径
const noHeaderPages = ref([
  '/login',
  '/register',
  '/article/'
  // 可以在这里添加其他不需要抬头的页面
])

// 初始化用户状态（检查记住我登录）
initUserState()

</script>

<template>
  <div id="app">
    <!-- 全局落花特效 -->
    <FallingFlowers />

    <!-- 全局抬头（登录注册、文章详情页面不显示） -->
    <GlobalHeader v-if="!noHeaderPages.some(path => $route.path.startsWith(path))" />

    <!-- 路由视图 -->
    <RouterView />
  </div>
</template>

<style>
#app {
  width: 100%;
  height: 100vh;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>
