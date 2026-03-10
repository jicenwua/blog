<template>
  <div class="statistics-content">
    <el-card class="statistics-card">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon"><DataAnalysis /></el-icon>
          <span>博客统计</span>
        </div>
      </template>

      <div class="statistics-body" style="flex: 1; overflow-y: auto;">
        <!-- 核心数据卡片 -->
        <el-row :gutter="20" class="stats-overview">
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.articleTotal }}</div>
                <div class="stat-label">文章总数</div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                <el-icon><View /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.viewTotal }}</div>
                <div class="stat-label">总访问量</div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ statistics.commentTotal }}</div>
                <div class="stat-label">评论总数</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-divider />

        <!-- 热门标签 -->
        <el-row :gutter="20" class="stats-detail">
          <el-col :span="24">
            <h3 class="section-title">
              <el-icon><PriceTag /></el-icon>
              热门标签 TOP10
            </h3>
            <div class="tag-cloud">
              <el-tag
                v-for="(tag, index) in hotTags"
                :key="tag.name"
                :type="getTagType(index)"
                :size="getTagSize(index)"
                class="hot-tag"
              >
                {{ tag.name }} ({{ tag.count }})
              </el-tag>
            </div>
          </el-col>
        </el-row>

        <el-divider />

        <!-- 分类统计 -->
        <el-row :gutter="20" class="stats-detail">
          <el-col :span="24">
            <h3 class="section-title">
              <el-icon><Folder /></el-icon>
              文章分类统计
            </h3>
            <el-table :data="categoryStats" style="width: 100%" border>
              <el-table-column prop="categoryName" label="分类名称" />
              <el-table-column prop="articleCount" label="文章数量" sortable align="center" />
              <el-table-column prop="viewCount" label="浏览次数" sortable align="center" />
            </el-table>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  DataAnalysis,
  Document,
  View,
  ChatDotRound,
  Folder,
  Calendar,
  PriceTag
} from '@element-plus/icons-vue'
import { getStatistics, getCategoryStatistics, getHotTags } from '@/api/article'

const statistics = ref({
  articleTotal: 0,
  viewTotal: 0,
  commentTotal: 0
})

// 分类统计
const categoryStats = ref([])

// 热门标签
const hotTags = ref([])

// 获取标签类型
const getTagType = (index) => {
  const types = ['primary', 'success', 'warning', 'danger', 'info']
  return types[index % types.length]
}

// 获取标签大小
const getTagSize = (index) => {
  if (index < 3) return 'large'
  if (index < 6) return 'medium'
  return 'small'
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const response = await getStatistics()
    if (response.code === 200 && response.data) {
      statistics.value = {
        articleTotal: response.data.articleTotal || 0,
        viewTotal: response.data.viewTotal || 0,
        commentTotal: response.data.commentTotal || 0
      }
    }
  } catch (error) {
    // 加载失败时静默处理，不影响其他功能
  }
}

// 加载分类统计
const loadCategoryStatistics = async () => {
  try {
    const response = await getCategoryStatistics()
    if (response.code === 200 && response.data) {
      categoryStats.value = response.data.map(item => ({
        categoryName: item.categoryName,
        articleCount: item.articleCount,
        viewCount: item.viewCount
      }))
    }
  } catch (error) {
    // 加载失败时静默处理，不影响其他功能
  }
}

// 加载热门标签
const loadHotTags = async () => {
  try {
    const response = await getHotTags(10)
    if (response.code === 200 && response.data) {
      hotTags.value = response.data.map(item => ({
        name: item.name,
        count: item.count
      }))
    }
  } catch (error) {
    // 加载失败时静默处理，不影响其他功能
  }
}

onMounted(() => {
  loadStatistics()
  loadCategoryStatistics()
  loadHotTags()
})
</script>

<style scoped>
.statistics-content {
  height: calc(100vh - 80px);
  overflow-x: hidden;
  overflow-y: auto;
  padding: 20px;
}

.statistics-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon {
  color: #f08080;
  font-size: 18px;
}

.stats-overview {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 28px;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.hot-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.hot-tag:hover {
  transform: translateY(-2px);
}
</style>
