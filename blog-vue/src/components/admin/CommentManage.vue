<template>
  <div class="comment-manage">
    <el-card class="manage-card">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon"><ChatDotRound /></el-icon>
          <span>评论管理</span>
        </div>
      </template>

      <!-- 评论列表 -->
      <el-table :data="comments" style="width: 100%" v-loading="loading" border>
        <el-table-column prop="id" label="评论 ID" width="180" />
        <el-table-column label="用户信息" width="200">
          <template #default="{ row }">
            <div class="user-info">
              <div class="user-detail">
                <div class="user-name">{{ row.userNickname }}</div>
                <div class="user-id">ID: {{ row.userId }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="likeCount" label="点赞数" width="80" align="center" sortable />
        <el-table-column prop="replyCount" label="回复数" width="80" align="center" sortable />
        <el-table-column label="创建时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          layout="total, prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ChatDotRound } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const comments = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 加载评论列表
const loadComments = async () => {
  try {
    loading.value = true
    const response = await request({
      url: '/article/admin/all',
      method: 'get',
      params: {
        page: currentPage.value - 1, // 后端从 0 开始
        size: pageSize.value
      }
    })

    if (response.code === 200 && response.data) {
      comments.value = response.data.content
      total.value = response.data.totalElements
    }
  } catch (error) {
    ElMessage.error('加载评论失败')
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page
  loadComments()
}

// 删除评论
const handleDelete = (commentId) => {
  ElMessageBox.confirm(
    '确定要删除该评论吗？删除后无法恢复。',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await request({
        url: `/article/comment/${commentId}`,
        method: 'delete'
      })

      if (response.code === 200) {
        ElMessage.success('删除成功')
        loadComments() // 重新加载列表
      } else {
        ElMessage.error('删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败：' + (error.message || '未知错误'))
    }
  }).catch(() => {
    // 用户取消
  })
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.comment-manage {
  height: 100%;
  padding: 20px;
  background: #f0f2f5;
}

.manage-card {
  min-height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}

.header-icon {
  color: #f08080;
  font-size: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-weight: 500;
  color: #303133;
}

.user-id {
  font-size: 12px;
  color: #909399;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
}
</style>
