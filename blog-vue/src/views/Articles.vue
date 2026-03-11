<template>
  <div class="articles-container">
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
        <!-- 文章列表 -->
        <div class="article-list">
          <el-card class="section-card">
            <template #header>
              <div class="card-header">
                <span class="section-title">全部文章</span>
              </div>
            </template>

            <!-- 文章项 -->
            <div v-for="article in articles" :key="article.id" class="article-item">
              <div class="article-image" @click="goToArticle(article.id)">
                <img :src="article.image" :alt="article.title" />
              </div>
              <div class="article-content">
                <div class="article-header">
                  <el-tag size="small" class="top-tag" v-if="article.isTop">置顶</el-tag>
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
                  <span class="meta-item">
                    <el-icon><Star /></el-icon>
                    {{ article.likes }}
                  </span>
                  <div class="article-tags">
                    <el-tag v-for="tag in article.tags" :key="tag" size="small" class="tag-item">
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
import { Clock, View, ChatDotRound, Star, DataAnalysis } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRotatingImage } from '@/utils/defaultImages'

const router = useRouter()
const currentCategory = ref('') // 默认为空表示全部分类
const articles = ref([])

// 静态分类数据 - 使用中文分类名，与 MongoDB 中的 category_id 一致
const categories = [
  { id: '', name: '全部分类' },
  { id: '后端', name: '后端' },
  { id: '缓存', name: '缓存' },
  { id: '数据库', name: '数据库' },
  { id: '消息中间件', name: '消息中间件' },
  { id: 'AI', name: 'AI' }
]

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 加载文章列表
const loadArticles = async () => {
  try {
    // 调用后端接口获取文章列表，支持按分类筛选
    const params = {
      page: currentPage.value - 1, // 后端从 0 开始
      size: pageSize.value
    }

    // 如果选择了分类，添加分类筛选参数
    if (currentCategory.value) {
      params.categoryId = currentCategory.value
    }

    const response = await request({
      url: '/article/list',
      method: 'get',
      params
    })

    if (response.code === 200 && response.data) {
      // 后端返回的是 Page 对象
      const pageData = response.data
      articles.value = pageData.content.map((article, index) => ({
        id: article.id,
        title: article.title,
        excerpt: article.content?.substring(0, 100) + '...' || '',
        image: getRotatingImage(), // 轮询获取图片
        date: formatArticleDate(article.createTime),
        views: article.viewCount || 0,
        comments: article.commentCount || 0,
        likes: article.likeCount || 0,
        isTop: article.isTop || false,
        tags: article.tags || []
      }))

      total.value = pageData.totalElements || pageData.content.length
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
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

// 去除 HTML 标签（使用正则表达式）
const stripHtml = (html) => {
  if (!html) return ''
  return html.replace(/<[^>]*>/g, '')
}

// 处理分类变化
const selectCategory = (categoryId) => {
  currentCategory.value = categoryId
  currentPage.value = 1 // 重置到第一页
  loadArticles()
}

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page
  loadArticles()
}

// 跳转到文章详情（新标签页打开）
const goToArticle = (articleId) => {
  const routeUrl = router.resolve({
    path: `/article/${articleId}`
  })
  window.open(routeUrl.href, '_blank')
}

// 页面加载时加载数据
onMounted(() => {
  loadArticles()
})
</script>

<style scoped>
.articles-container {
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

.category-item::-webkit-scrollbar {
  width: 6px;
}

.category-item::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}

.category-item::-webkit-scrollbar-thumb:hover {
  background: #c0c4cc;
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

.article-list {
  min-width: 0;
  max-width: 1200px;
  margin: 0 auto;
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

.filter-section {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.category-select {
  width: 200px;
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

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-card,
.sidebar-list-card {
  margin-bottom: 20px;
}

.header-icon {
  color: #f08080;
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
