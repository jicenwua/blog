<template>
  <div class="about-container">
    <!-- 主内容区 -->
    <el-main class="main-content">
      <div class="about-wrapper">
        <el-card class="about-card">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><User /></el-icon>
              <span>关于我</span>
            </div>
          </template>

          <div class="about-content">
            <div class="about-avatar">
              <el-avatar :size="150" :src="avatar" />
            </div>

            <div class="about-info">
              <h1 class="about-title">欢迎来到个人博客</h1>
              <p class="about-description">
                这里是一个热爱编程、热爱分享的程序员的小天地。我主要分享 Java、前端开发、工具技巧等方面的知识和经验。
              </p>

              <el-divider />

              <div class="info-section">
                <h3 class="section-title">
                  <el-icon><UserFilled /></el-icon>
                  个人信息
                </h3>
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="昵称">晶哥</el-descriptions-item>
                  <el-descriptions-item label="职业">软件工程师</el-descriptions-item>
                  <el-descriptions-item label="技术栈">Java, Vue, Spring Boot</el-descriptions-item>
                  <el-descriptions-item label="所在地">中国</el-descriptions-item>
                </el-descriptions>
              </div>

              <div class="info-section">
                <h3 class="section-title">
                  <el-icon><Trophy /></el-icon>
                  技能专长
                </h3>
                <div class="skills">
                  <el-tag size="large" class="skill-tag">Java</el-tag>
                  <el-tag size="large" class="skill-tag" type="success">Spring Boot</el-tag>
                  <el-tag size="large" class="skill-tag" type="warning">Vue.js</el-tag>
                  <el-tag size="large" class="skill-tag" type="danger">MySQL</el-tag>
                  <el-tag size="large" class="skill-tag" type="info">Redis</el-tag>
                  <el-tag size="large" class="skill-tag">Docker</el-tag>
                  <el-tag size="large" class="skill-tag">Git</el-tag>
                  <el-tag size="large" class="skill-tag">Linux</el-tag>
                </div>
              </div>

              <div class="info-section">
                <h3 class="section-title">
                  <el-icon><Message /></el-icon>
                  联系方式
                </h3>
                <div class="social-links">
                  <el-button type="primary" :icon="ChatDotRound">QQ</el-button>
                  <el-button type="success" :icon="ChatDotRound">微信</el-button>
                  <el-button type="warning" :icon="Github">GitHub</el-button>
                </div>
              </div>

              <el-divider />

              <div class="info-section">
                <h3 class="section-title">
                  <el-icon><Document /></el-icon>
                  博客说明
                </h3>
                <div class="blog-intro">
                  <p>
                    这个博客主要用于记录和分享我在学习和工作中的经验和心得。内容涵盖:
                  </p>
                  <ul class="intro-list">
                    <li>
                      <el-icon><Check /></el-icon>
                      <strong>Java 开发</strong> - JDK 基础、Spring 全家桶、微服务架构等
                    </li>
                    <li>
                      <el-icon><Check /></el-icon>
                      <strong>前端开发</strong> - Vue、React、Element Plus 等现代前端技术
                    </li>
                    <li>
                      <el-icon><Check /></el-icon>
                      <strong>工具技巧</strong> - IDEA 插件、效率工具、开发环境配置等
                    </li>
                    <li>
                      <el-icon><Check /></el-icon>
                      <strong>编程心得</strong> - 代码规范、设计模式、项目经验等
                    </li>
                  </ul>
                </div>
              </div>

              <div class="info-section">
                <h3 class="section-title">
                  <el-icon><Star /></el-icon>
                  统计数据
                </h3>
                <el-row :gutter="20" class="stats-row">
                  <el-col :span="6">
                    <div class="stat-box">
                      <div class="stat-number">226</div>
                      <div class="stat-label">文章总数</div>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="stat-box">
                      <div class="stat-number">14</div>
                      <div class="stat-label">分类数量</div>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="stat-box">
                      <div class="stat-number">15</div>
                      <div class="stat-label">评论数量</div>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="stat-box">
                      <div class="stat-number">1000+</div>
                      <div class="stat-label">访问次数</div>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </el-main>

    <!-- 页脚 -->
    <el-footer class="footer">
      <div class="footer-content">
        <p>© 2024 个人博客 - Powered by Vue & Element Plus</p>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  UserFilled,
  Trophy,
  Message,
  Check,
  Star,
  Github
} from '@element-plus/icons-vue'

const searchQuery = ref('')
const avatar = 'https://via.placeholder.com/150x150.png?text=Avatar'

// 检查登录状态
const checkLoginStatus = () => {
  isLoggedIn.value = checkLoggedIn()
  if (isLoggedIn.value) {
    userInfo.value = getUserInfo()
    // 检查是否为管理员
    isAdmin.value = hasRole('admin')
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await userLogout()
    isLoggedIn.value = false
    userInfo.value = null
    ElMessage.success('已退出登录')
    router.push('/')
  } catch (error) {
    console.error('退出登录失败:', error)
    // 即使接口调用失败，也清除本地状态
    isLoggedIn.value = false
    userInfo.value = null
    ElMessage.success('已退出登录')
    router.push('/')
  }
}

// 页面加载时检查登录状态
onMounted(() => {
  checkLoginStatus()
})

// 用户状态
const userInfo = ref(null)
const isLoggedIn = ref(false)
</script>

<style scoped>
.about-container {
  min-height: 100vh;
  background-color: #f0f2f5;
  background-image:
    linear-gradient(to right, rgba(0, 0, 0, 0.05) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(0, 0, 0, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  background-attachment: fixed;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 20px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 10px;
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

.search-section {
  width: 250px;
}

.search-input {
  width: 100%;
}

.main-content {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.about-wrapper {
  max-width: 900px;
  margin: 0 auto;
}

.about-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon {
  color: #f08080;
}

.about-content {
  padding: 20px 0;
}

.about-avatar {
  text-align: center;
  margin-bottom: 30px;
}

.about-title {
  text-align: center;
  font-size: 28px;
  color: #303133;
  margin-bottom: 15px;
}

.about-description {
  text-align: center;
  font-size: 16px;
  color: #606266;
  line-height: 1.8;
  max-width: 700px;
  margin: 0 auto 30px;
}

.info-section {
  margin-bottom: 30px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f08080;
}

.skills {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.skill-tag {
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.skill-tag:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.social-links {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.social-links .el-button {
  padding: 12px 24px;
  font-size: 14px;
}

.blog-intro {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.intro-list {
  list-style: none;
  padding: 0;
  margin: 15px 0;
}

.intro-list li {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 12px;
  padding: 10px;
  background: #fff;
  border-radius: 6px;
  transition: all 0.3s;
}

.intro-list li:hover {
  transform: translateX(5px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.intro-list li .el-icon {
  color: #67c23a;
  margin-top: 4px;
}

.stats-row {
  margin-top: 15px;
}

.stat-box {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: #fff;
  transition: all 0.3s;
}

.stat-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.4);
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.footer {
  background: #fff;
  padding: 20px;
  text-align: center;
  color: #909399;
  font-size: 13px;
  margin-top: 40px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

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
  
  .auth-section {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-left: 20px;
  }
  
  .auth-btn {
    font-size: 14px;
    padding: 8px 16px;
    color: #409EFF;
  }
  
  .auth-btn:hover {
    color: #66b1ff;
  }
  
  /* 管理员标识 */
  .admin-badge {
    display: flex;
    align-items: center;
    margin-left: 20px;
  }
  
  /* 用户信息下拉 */
  .user-section {
    display: flex;
    align-items: center;
    margin-left: 20px;
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 12px;
    border-radius: 20px;
    background: #f5f7fa;
  }
  
  .user-name {
    font-size: 14px;
    color: #606266;
    font-weight: 500;
  }
}

.stats-row {
  .el-col {
    margin-bottom: 15px;
  }
}
</style>
