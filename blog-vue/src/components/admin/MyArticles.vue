<template>
  <div class="my-articles-content">
    <el-card class="articles-card">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon"><Document /></el-icon>
          <span>我的文章</span>
          <el-button type="primary" style="margin-left: auto;" @click="handlePublish">
            <el-icon><Plus /></el-icon>
            发布文章
          </el-button>
        </div>
      </template>

      <div class="articles-body" style="flex: 1; overflow-y: auto;">
        <!-- 文章列表表格 -->
        <el-table :data="articleList" style="width: 100%" border v-loading="loading">
          <el-table-column prop="title" label="文章标题" min-width="250" show-overflow-tooltip />
          <el-table-column prop="categoryName" label="分类" width="100" align="center" />
          <el-table-column label="标签" width="200" align="center">
            <template #default="{ row }">
              <el-tag v-for="tag in row.tags" :key="tag" size="small" style="margin-right: 4px; margin-bottom: 4px;">
                {{ tag }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览量" width="90" sortable align="center" />
          <el-table-column prop="likeCount" label="点赞数" width="90" sortable align="center" />
          <el-table-column label="创建时间" width="160" align="center">
            <template #default="{ row }">
              {{ formatDate(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" size="small" text @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" size="small" text @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Document, Edit, Delete, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getMyArticles, deleteArticle } from '@/api/article'

const router = useRouter()
const loading = ref(false)
const articleList = ref([])

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载文章列表
const loadArticles = async () => {
  loading.value = true
  try {
    const res = await getMyArticles()
    if (res.code === 200) {
      articleList.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载文章列表失败')
  } finally {
    loading.value = false
  }
}

// 发布文章
const handlePublish = () => {
  router.push('/admin/publish?from=/statistics&menu=2')
}

// 编辑文章
const handleEdit = (row) => {
  router.push(`/admin/publish?id=${row.id}&from=/statistics&menu=2`)
}

// 删除文章
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除文章《${row.title}》吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteArticle(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 重新加载列表
        loadArticles()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 用户取消
  })
}

onMounted(() => {
  loadArticles()
})
</script>

<style scoped>
.my-articles-content {
  height: calc(100vh - 80px);
  overflow: hidden;
  padding: 20px;
}

.articles-card {
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

.articles-body {
  flex: 1;
  overflow-y: auto;
}

.el-table {
  margin-top: 10px;
}
</style>
