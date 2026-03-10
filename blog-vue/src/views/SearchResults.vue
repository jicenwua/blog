<template>
  <div class="search-results-container">
    <!-- 左侧悬浮分类卡片 -->
    <div class="category-sidebar-card">
      <el-card class="category-card">
        <template #header>
          <div class="category-header">
            <el-icon class="header-icon"><DataAnalysis /></el-icon>
            <span>文章分类</span>
          </div>
        </template>

        <div class="category-list">
          <div
            v-for="category in categories"
            :key="category.id"
            class="category-item"
            :class="{ active: currentCategory === category.id }"
            @click="selectCategory(category.id)"
          >
            {{ category.name }}
          </div>
        </div>
      </el-card>
    </div>

    <!-- 主内容区 -->
    <el-main class="main-content">
      <div class="content-wrapper">
        <!-- 搜索提示 -->
        <el-card class="search-info-card">
          <div class="search-info">
            <el-icon class="search-icon"><Search /></el-icon>
            <span class="search-keyword">"{{ keyword }}"</span>
            <span class="search-result-count">的搜索结果</span>
            <span class="result-count">{{ total }} 条结果</span>
          </div>
        </el-card>

        <!-- 文章列表 -->
        <div class="article-list">
          <el-card class="section-card">
            <template #header>
              <div class="card-header">
                <span class="section-title">相关文章</span>
              </div>
            </template>

            <!-- 加载中 -->
            <div v-if="loading" class="loading-container">
              <el-empty description="正在搜索..." :image-size="80" />
            </div>

            <!-- 无结果 -->
            <div v-else-if="articles.length === 0" class="empty-container">
              <el-empty description="未找到相关文章">
                <el-button type="primary" @click="goBack">返回首页</el-button>
              </el-empty>
            </div>

            <!-- 文章项 -->
            <div v-else>
              <div v-for="article in articles" :key="article.id" class="article-item">
                <div class="article-image" @click="goToArticle(article.id)">
                  <img :src="article.image" :alt="article.title" />
                </div>
                <div class="article-content">
                  <div class="article-header">
                    <h3 class="article-title" @click="goToArticle(article.id)" v-html="highlightText(article.title)"></h3>
                  </div>
                  <p class="article-excerpt" v-html="highlightText(article.excerpt)"></p>
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
                    <span class="meta-item">
                      <el-icon><Star /></el-icon>
                      {{ article.likes }}
                    </span>
                    <div class="article-tags">
                      <el-tag v-for="tag in article.tags" :key="tag" size="small" class="tag-item">
                        <span v-html="highlightText(tag)"></span>
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
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Clock, View, ChatDotRound, Star, DataAnalysis, Search } from '@element-plus/icons-vue'
import { searchArticles } from '@/api/article'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const articles = ref([])
const loading = ref(false)
const currentCategory = ref('')

// 静态分类数据
const categories = [
  { id: '', name: '全部分类' },
  { id: 'backend', name: '后端' },
  { id: 'cache', name: '缓存' },
  { id: 'database', name: '数据库' },
  { id: 'middleware', name: '消息中间件' },
  { id: 'ai', name: 'AI' }
]

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 高亮关键词 - 返回 HTML 用于 v-html
const highlightText = (text) => {
  if (!text || !keyword.value) return text || ''
  // 转义 HTML 特殊字符，避免 XSS 攻击
  const escapedText = text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
  // 高亮关键词
  const regex = new RegExp(`(${keyword.value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi')
  return escapedText.replace(regex, '<span class="highlight">$1</span>')
}

// 加载搜索结果
const loadSearchResults = async () => {
  const searchKeyword = route.query.keyword
  if (!searchKeyword || !searchKeyword.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  keyword.value = searchKeyword.trim()
  loading.value = true

  try {
    const params = {
      keyword: keyword.value,
      page: currentPage.value - 1, // 后端从 0 开始
      size: pageSize.value
    }

    const response = await searchArticles(params)

    if (response.code === 200 && response.data) {
      const pageData = response.data
      articles.value = pageData.content.map(doc => ({
        id: doc.id,
        title: doc.title,
        excerpt: doc.content?.substring(0, 100) + '...' || '',
        image: 'https://picsum.photos/400/250',
        date: formatArticleDate(doc.createTime),
        views: doc.viewCount || 0,
        comments: 0, // Elasticsearch 中没有评论数，暂时设为 0
        likes: doc.likeCount || 0,
        tags: doc.tags || [],
        categoryName: doc.categoryName
      }))

      total.value = pageData.totalElements || pageData.content.length
    }
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    loading.value = false
  }
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

// 处理分类变化
const selectCategory = (categoryId) => {
  currentCategory.value = categoryId
  // 搜索结果页面暂时不支持按分类筛选，可以后续扩展
  ElMessage.info('搜索结果页面暂不支持分类筛选')
}

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page
  loadSearchResults()
}

// 跳转到文章详情
const goToArticle = (articleId) => {
  const routeUrl = router.resolve({
    path: `/article/${articleId}`
  })
  window.open(routeUrl.href, '_blank')
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 监听路由变化，重新加载搜索结果
watch(() => route.query.keyword, (newVal) => {
  if (newVal && newVal.trim()) {
    currentPage.value = 1
    loadSearchResults()
  }
})

onMounted(() => {
  loadSearchResults()
})
</script>

<style scoped>
.search-results-container {
  min-height: 100vh;
  background-color: #f0f2f5;
  background-image:
    linear-gradient(to right, rgba(0, 0, 0, 0.05) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(0, 0, 0, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  background-attachment: fixed;
  position: relative;
}

/* 左侧悬浮分类卡片 */
.category-sidebar-card {
  position: fixed;
  left: calc(50% - 620px);
  top: 100px;
  width: 200px;
  z-index: 100;
}

.category-card {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
}

.category-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: bold;
  color: #303133;
}

.header-icon {
  color: #f08080;
  font-size: 18px;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 600px;
  overflow-y: auto;
}

.category-item {
  padding: 10px 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  color: #606266;
  background: #f5f7fa;
}

.category-item:hover {
  background: #ecf5ff;
  color: #409EFF;
  transform: translateX(5px);
}

.category-item.active {
  background: #409EFF;
  color: #fff;
  font-weight: 500;
}

.main-content {
  padding: 20px;
  padding-left: 240px;
  max-width: 1200px;
  margin: 0 auto;
}

.content-wrapper {
  display: block;
}

.search-info-card {
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
}

.search-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  color: #606266;
}

.search-icon {
  color: #409EFF;
  font-size: 20px;
}

.search-keyword {
  font-weight: bold;
  color: #409EFF;
  font-size: 18px;
}

.search-result-count {
  color: #909399;
}

.result-count {
  margin-left: auto;
  font-weight: bold;
  color: #67C23A;
}

.article-list {
  min-width: 0;
  max-width: 1200px;
  margin: 0 auto;
}

.section-card {
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
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

.loading-container,
.empty-container {
  display: flex;
  justify-content: center;
  padding: 60px 0;
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
  flex-wrap: wrap;
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

/* 高亮文本 */
.highlight {
  color: #f56c6c;
  font-weight: bold;
  background-color: #fef0f0;
  padding: 0 4px;
  border-radius: 2px;
}

@media (max-width: 768px) {
  .category-sidebar-card {
    display: none;
  }

  .main-content {
    padding-left: 20px;
  }

  .article-list {
    width: 100%;
  }

  .article-item {
    flex-direction: column;
  }

  .article-image {
    width: 100%;
    height: 200px;
  }
}
</style>
