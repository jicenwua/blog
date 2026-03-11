<template>
  <div class="blog-container">
    <!-- 落花特效 -->
    <div class="falling-flowers" ref="flowerContainer"></div>

    <!-- 主内容区 -->
    <el-main class="main-content">
      <div class="content-wrapper">
        <!-- 左侧文章列表 -->
        <div class="article-list">
          <el-card class="section-card">
            <template #header>
              <div class="card-header">
                <span class="section-title">最新文章</span>
              </div>
            </template>

            <!-- 文章项 -->
            <div v-for="article in articles" :key="article.id" class="article-item">
              <div class="article-image" @click="goToArticle(article.id)">
                <img :src="article.image" :alt="article.title" />
              </div>
              <div class="article-content">
                <div class="article-header">
                  <h3 class="article-title" @click="goToArticle(article.id)">{{ article.title }}</h3>
                </div>
                <p class="article-excerpt">{{ stripHtml(article.excerpt) }}</p>
                <div class="article-meta">
                  <span class="meta-item">
                    <el-icon><Clock /></el-icon>
                    {{ article.date }}
                  </span>
                  <span class="meta-item">
                    <el-icon><View /></el-icon>
                    {{ article.views }}
                  </span>
                  <span class="meta-item">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ article.comments }}
                  </span>
                  <div class="article-tags">
                    <el-tag v-for="tag in article.tags" :key="tag" size="small" class="tag-item" @click.stop>
                      {{ tag }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>

            <!-- 分页 -->
            <div class="pagination">
              <el-pagination
                layout="prev, pager, next"
                :total="total"
                :page-size="pageSize"
                :current-page="currentPage"
                @current-change="handlePageChange"
              />
            </div>
          </el-card>
        </div>

        <!-- 右侧边栏 -->
        <div class="sidebar">
          <!-- 公告信息 -->
          <el-card class="notice-card">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><Bell /></el-icon>
                <span>公告信息</span>
              </div>
            </template>
            <div class="notice-content">
              <p>【Ctrl/Command + D】收藏本站</p>
            </div>
          </el-card>

          <!-- 人生倒计时 -->
          <el-card class="sidebar-card">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><Timer /></el-icon>
                <span>人生倒计时</span>
              </div>
            </template>
            <div class="countdown-content">
              <div class="countdown-item">
                <div class="countdown-label">今日已经过去 <span class="countdown-time">{{ formatTimeUnit('today', countdownData.today.elapsed) }}</span></div>
                <el-progress :percentage="countdownData.today.percentage" :show-text="false" stroke-width="8" />
                <div class="countdown-percent">{{ Math.round(countdownData.today.percentage) }}%</div>
              </div>

              <div class="countdown-item">
                <div class="countdown-label">这周已经过去 <span class="countdown-time">{{ formatTimeUnit('week', countdownData.week.elapsed) }}</span></div>
                <el-progress :percentage="countdownData.week.percentage" :show-text="false" stroke-width="8" status="warning" />
                <div class="countdown-percent">{{ Math.round(countdownData.week.percentage) }}%</div>
              </div>

              <div class="countdown-item">
                <div class="countdown-label">本月已经过去 <span class="countdown-time">{{ formatTimeUnit('month', countdownData.month.elapsed) }}</span></div>
                <el-progress :percentage="countdownData.month.percentage" :show-text="false" stroke-width="8" color="#ff6b6b" />
                <div class="countdown-percent">{{ Math.round(countdownData.month.percentage) }}%</div>
              </div>

              <div class="countdown-item">
                <div class="countdown-label">今年已经过去 <span class="countdown-time">{{ formatTimeUnit('year', countdownData.year.elapsed) }}</span></div>
                <el-progress :percentage="countdownData.year.percentage" :show-text="false" stroke-width="8" color="#51cf66" />
                <div class="countdown-percent">{{ Math.round(countdownData.year.percentage) }}%</div>
              </div>
            </div>
          </el-card>
        </div>
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
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Clock,
  View,
  ChatDotRound,
  Bell,
  Timer
} from '@element-plus/icons-vue'
import { getArticleList } from '@/api/article'
import { ElMessage } from 'element-plus'
import { getUserInfo, isLoggedIn as checkLoggedIn, hasRole, logout as userLogout } from '@/utils/auth'
import { getRotatingImage } from '@/utils/defaultImages'

const searchQuery = ref('')
let countdownInterval = null
const router = useRouter()

// 用户状态
const userInfo = ref(null)
const isLoggedIn = ref(false)
const isAdmin = ref(false)

// 时间进度数据
const countdownData = ref({
  today: { elapsed: 0, percentage: 0 },
  week: { elapsed: 0, percentage: 0 },
  month: { elapsed: 0, percentage: 0 },
  year: { elapsed: 0, percentage: 0 }
})

// 计算时间进度
const calculateCountdown = () => {
  const now = new Date()

  // 今日进度（精确到小时）
  const startOfDay = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const hoursPassedToday = (now - startOfDay) / 1000 / 3600
  const todayPercentage = (hoursPassedToday / 24) * 100
  countdownData.value.today = {
    elapsed: Math.floor(hoursPassedToday),
    percentage: parseFloat(todayPercentage.toFixed(0))
  }

  // 本周进度（从周一开始，精确到天）
  const dayOfWeek = now.getDay() || 7 // 周日为 7
  const startOfWeek = new Date(startOfDay)
  startOfWeek.setDate(startOfWeek.getDate() - (dayOfWeek - 1))
  const daysPassedInWeek = (startOfDay - startOfWeek) / 1000 / 60 / 60 / 24 + hoursPassedToday / 24
  const weekPercentage = (daysPassedInWeek / 7) * 100
  countdownData.value.week = {
    elapsed: Math.floor(daysPassedInWeek),
    percentage: parseFloat(weekPercentage.toFixed(0))
  }

  // 本月进度（精确到天）
  const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)
  const daysInMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0).getDate()
  const daysPassedInMonth = (startOfDay - startOfMonth) / 1000 / 60 / 60 / 24 + hoursPassedToday / 24
  const monthPercentage = (daysPassedInMonth / daysInMonth) * 100
  countdownData.value.month = {
    elapsed: Math.floor(daysPassedInMonth),
    percentage: parseFloat(monthPercentage.toFixed(0))
  }

  // 今年进度（精确到月）
  const currentMonth = now.getMonth() // 0-11
  const monthsPassedInYear = currentMonth + (now.getDate() - 1) / 30
  const yearPercentage = (monthsPassedInYear / 12) * 100
  countdownData.value.year = {
    elapsed: Math.floor(monthsPassedInYear),
    percentage: parseFloat(yearPercentage.toFixed(0))
  }
}

// 格式化时间单位
const formatTimeUnit = (type, value) => {
  switch (type) {
    case 'today':
      return `${value}小时`
    case 'week':
      return `${value}天`
    case 'month':
      return `${value}天`
    case 'year':
      return `${value}个月`
    default:
      return value
  }
}

onMounted(() => {
  calculateCountdown() // 初始计算
  countdownInterval = setInterval(() => {
    calculateCountdown() // 每秒更新
  }, 1000)

  // 加载文章列表
  loadArticles()

  // 检查登录状态
  checkLoginStatus()
})

onUnmounted(() => {
  if (countdownInterval) {
    clearInterval(countdownInterval)
  }
})

// 文章数据
const articles = ref([])

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 加载文章列表
const loadArticles = async () => {
  try {
    const res = await getArticleList({
      page: currentPage.value - 1, // 后端从 0 开始
      size: pageSize.value
    })

    if (res.code === 200 && res.data) {
      // 后端返回的是 Page 对象，需要提取 content
      const pageData = res.data
      const articlesList = pageData.content || pageData || []

      // 按观看数量降序排序
      articles.value = articlesList.map((article, index) => ({
        id: article.id,
        title: article.title,
        excerpt: article.content?.substring(0, 100) + '...' || '',
        image: getRotatingImage(), // 轮询获取图片
        date: formatArticleDate(article.createTime),
        views: article.viewCount || 0,
        comments: article.commentCount || 0,
        tags: article.tags || []
      })).sort((a, b) => b.views - a.views)

      // 计算总数
      total.value = pageData.totalElements || pageData.total || articlesList.length
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
  }
}

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page
  loadArticles()
}

// 格式化文章时间
const formatArticleDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 去除 HTML 标签（使用正则表达式）
const stripHtml = (html) => {
  if (!html) return ''
  return html.replace(/<[^>]*>/g, '')
}

// 检查登录状态
const checkLoginStatus = () => {
  isLoggedIn.value = checkLoggedIn()
  if (isLoggedIn.value) {
    userInfo.value = getUserInfo()
    // 检查是否为管理员
    isAdmin.value = hasRole('ADMIN')
  }
}

// 跳转到文章详情页（新标签页打开）
const goToArticle = (articleId) => {
  const routeUrl = router.resolve({
    path: `/article/${articleId}`
  })
  window.open(routeUrl.href, '_blank')
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

// 页面加载时检查登录状态（不再需要）
// onMounted(() => {
//   checkLoginStatus()
// })
</script>

<style scoped>
.blog-container {
  min-height: 100vh;
  width: 100%;
  background-color: #f0f2f5;
  background-image:
    linear-gradient(to right, rgba(0, 0, 0, 0.05) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(0, 0, 0, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  background-attachment: fixed;
  position: relative;
  overflow-x: hidden;
}

/* 樱花飘落效果 */
.blog-container::before {
  content: '';
  position: fixed;
  top: -10%;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    radial-gradient(circle, rgba(255, 183, 197, 0.4) 1px, transparent 1px),
    radial-gradient(circle, rgba(255, 183, 197, 0.4) 1px, transparent 1px);
  background-size: 50px 50px, 35px 35px;
  background-position: 0 0, 25px 25px;
  animation: sakura 60s linear infinite;
  pointer-events: none;
  z-index: 0;
  opacity: 0.3;
}

@keyframes sakura {
  0% {
    transform: translateY(-10%) rotate(0deg);
  }
  100% {
    transform: translateY(100%) rotate(360deg);
  }
}

/* 头部样式 */
.main-content {
  padding: 20px;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 20px;
  width: 100%;
}

/* 文章列表 */
.article-list {
  min-width: 0;
}

.section-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #f08080;
}

.article-item {
  display: flex;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.article-item:last-child {
  border-bottom: none;
}

.article-image {
  flex-shrink: 0;
  width: 200px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}

.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.article-image img:hover {
  transform: scale(1.1);
}

.article-content {
  flex: 1;
  min-width: 0;
}

.article-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.article-title {
  font-size: 16px;
  color: #303133;
  margin: 0;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.3s;
}

.article-title:hover {
  color: #409EFF;
}

.article-excerpt {
  font-size: 13px;
  color: #909399;
  line-height: 1.6;
  margin: 10px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
  font-size: 12px;
  color: #909399;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.article-tags {
  display: flex;
  gap: 5px;
  margin-left: auto;
}

.tag-item {
  cursor: pointer;
  transition: all 0.3s;
}

.tag-item:hover {
  transform: translateY(-2px);
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

/* 侧边栏 */
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-card {
  position: relative;
  overflow: hidden;
  padding: 0;
  background: transparent;
  box-shadow: none;
  border: none;
}

.profile-background {
  height: 120px;
  overflow: hidden;
}

.profile-background img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-info {
  padding: 20px;
  text-align: center;
}

.stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 15px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.social-links {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.social-icon {
  font-size: 20px;
  color: #606266;
  cursor: pointer;
  transition: color 0.3s;
}

.social-icon:hover {
  color: #409EFF;
}

.notice-card {
  background: linear-gradient(135deg, #fff5f5 0%, #fff 100%);
}

.notice-content {
  color: #f08080;
  font-size: 13px;
}

.sidebar-list-card,
.sidebar-card {
  margin-bottom: 20px;
}

.header-icon {
  color: #f08080;
}

.countdown-content {
  padding: 10px 0;
}

.countdown-item {
  margin-bottom: 24px;
  position: relative;
}

.countdown-item:last-child {
  margin-bottom: 0;
}

.countdown-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
  line-height: 1.6;
  font-weight: 400;
}

.countdown-time {
  color: #ff6b6b;
  font-weight: 500;
  margin-left: 4px;
}

.countdown-percent {
  position: absolute;
  right: 0;
  top: 32px;
  font-size: 14px;
  color: #999;
  font-weight: 400;
}

/* 自定义进度条样式 */
.countdown-item .el-progress {
  margin-right: 50px;
}

.countdown-item .el-progress__outer {
  border-radius: 10px;
}

.countdown-item .el-progress__bar {
  border-radius: 10px;
  background: #f0f0f0 !important;
}

.countdown-item .el-progress__inner {
  border-radius: 10px;
  transition: width 0.3s ease;
}

/* 页脚 */
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

/* 响应式 */
@media (max-width: 1024px) {
  .content-wrapper {
    grid-template-columns: 1fr 300px;
  }
}

@media (max-width: 768px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }

  .sidebar {
    order: -1;
  }

  .article-item {
    flex-direction: column;
  }

  .article-image {
    width: 100%;
    height: 200px;
  }

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
