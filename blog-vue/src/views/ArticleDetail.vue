                                                                                                                                                                                                                               <template>
  <div class="article-detail-container">
    <!-- 文章内容区 -->
    <el-main class="main-content">
      <div class="article-wrapper" v-loading="loading">
        <!-- 文章详情模式 -->
        <div class="article-content-wrapper" v-if="article && !isEditMode">
          <!-- 返回按钮 -->
          <div class="back-button-container">
            <el-button @click="$router.push('/articles')">
              <el-icon><Back /></el-icon>
              返回文章列表
            </el-button>
          </div>

          <!-- 左侧悬浮按钮 -->
          <div class="side-floating-buttons">
            <el-button
              circle
              class="floating-btn"
              :class="{ 'liked': hasLiked }"
              @click="handleLike"
              :loading="liking"
            >
              <el-icon><Star /></el-icon>
            </el-button>
            <el-button circle class="floating-btn" @click="scrollToComments">
              <el-icon><ChatDotRound /></el-icon>
            </el-button>
            <el-button circle class="floating-btn" @click="handleShare">
              <el-icon><Share /></el-icon>
            </el-button>
          </div>

          <!-- 文章头部信息 -->
          <div class="article-header-section">
            <!-- 标签 -->
            <div class="article-tags" v-if="article.tags && article.tags.length > 0">
              <el-tag
                v-for="tag in article.tags"
                :key="tag"
                size="small"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
            </div>

            <h1 class="article-title">{{ article.title }}</h1>
            <div class="article-meta">
              <div class="author-info">
                <el-avatar :size="40" :src="article.authorAvatar" v-if="article.authorAvatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <div class="author-details">
                  <span class="author-name">{{ article.authorName }}</span>
                  <div class="meta-details">
                    <span class="meta-item">
                      <el-icon><Clock /></el-icon>
                      {{ formatTime(article.createTime) }}
                    </span>
                    <span class="meta-divider">/</span>
                    <span class="meta-item">
                      <el-icon><View /></el-icon>
                      {{ article.viewCount }} 阅读
                    </span>
                    <span class="meta-divider">/</span>
                    <span class="meta-item">
                      <el-icon><Star /></el-icon>
                      {{ article.likeCount }} 点赞
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 温馨提示 -->
          <div class="warm-tip" v-if="article.updateTime">
            <el-icon><Warning /></el-icon>
            <span>温馨提示：本文最后更新于 {{ formatTime(article.updateTime) }}，若内容或图片失效，请留言反馈。部分素材来自网络，若不小心影响到您的利益，请联系我们删除。</span>
          </div>

          <!-- 文章内容 -->
          <div class="article-content" v-html="article.content"></div>
        </div>

        <!-- 评论区 -->
        <div class="comments-section" v-if="article && !isEditMode">
          <!-- 发表评论 -->
          <el-card class="comment-form-card">
            <template #header>
              <div class="form-header">
                <el-icon class="header-icon"><Edit /></el-icon>
                <span>发表评论</span>
              </div>
            </template>

            <el-form
              ref="commentFormRef"
              :model="commentForm"
              :rules="commentFormRules"
              class="comment-form"
            >
              <el-form-item prop="content">
                <el-input
                  v-model="commentForm.content"
                  type="textarea"
                  :rows="4"
                  placeholder="写下你的评论..."
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitComment" :loading="submitting">
                  <el-icon><Check /></el-icon>
                  发表评论
                </el-button>
                <el-button @click="resetForm">
                  <el-icon><Refresh /></el-icon>
                  重置
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 评论列表 -->
          <el-card class="comments-card">
            <template #header>
              <div class="comments-header">
                <el-icon class="header-icon"><ChatDotRound /></el-icon>
                <span>评论列表</span>
                <span class="comments-count">({{ comments.length }})</span>
              </div>
            </template>

            <!-- 评论列表 -->
            <div class="comments-list">
              <div v-if="comments.length === 0" class="no-comments">
                <el-empty description="暂无评论，快来抢沙发吧~" :image-size="80" />
              </div>
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-avatar">
                  <el-avatar :size="50" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png">
                  </el-avatar>
                </div>
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-author">{{ comment.userNickname }}</span>
                    <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                  </div>
                  <div class="comment-text">{{ comment.content }}</div>
                  <div class="comment-actions">
                    <el-button text size="small" @click="handleReply(comment)">
                      <el-icon><ChatLineRound /></el-icon>
                      回复
                    </el-button>
                    <el-button text size="small" @click="handleLikeComment(comment)">
                      <el-icon><Star /></el-icon>
                      {{ comment.likeCount || 0 }}
                    </el-button>
                  </div>

                  <!-- 回复列表 -->
                  <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
                    <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                      <div class="reply-avatar">
                        <el-avatar :size="40" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png">
                        </el-avatar>
                      </div>
                      <div class="reply-content">
                        <div class="reply-header">
                          <span class="reply-author">{{ reply.userNickname }}</span>
                          <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                        </div>
                        <div class="reply-text">
                          <span v-if="reply.parentUserName" class="reply-to">@{{ reply.parentUserName }}：</span>
                          {{ reply.content }}
                        </div>
                        <div class="reply-actions">
                          <el-button text size="small" @click="handleReply(reply)">
                            <el-icon><ChatLineRound /></el-icon>
                            回复
                          </el-button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  User,
  Clock,
  View,
  Back,
  Close,
  Check,
  Refresh,
  Warning,
  ChatDotRound,
  ChatLineRound,
  Share,
  Star
} from '@element-plus/icons-vue'
import { getUserInfo, isLoggedIn as checkLoggedIn, hasRole } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const liking = ref(false)
const article = ref(null)
const userInfo = ref(null)
const isLoggedIn = ref(false)
const isAdmin = ref(false)

// 评论相关
const comments = ref([])
const commentFormRef = ref(null)
const submitting = ref(false)
const commentForm = reactive({
  content: '',
  parentId: null,
  parentUserName: null
})

const commentFormRules = {
  content: [
    { required: true, message: '请输入评论内容', trigger: 'blur' },
    { min: 1, max: 500, message: '评论长度应在 1-500 个字符之间', trigger: 'blur' }
  ]
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载文章详情
const loadArticle = async () => {
  loading.value = true
  try {
    const response = await request({
      url: `/article/xq/${route.params.id}`,
      method: 'get'
    })

    if (response.code === 200 && response.data) {
      article.value = response.data
      // 设置页面标题为文章标题
      document.title = article.value.title + ' - 个人博客'
      // 增加浏览次数（异步，不阻塞）
      request({
        url: `/article/view/${route.params.id}`,
        method: 'post'
      }).catch(err => console.error('增加浏览失败:', err))

      // 检查是否已点赞
      await checkHasLiked()

      // 加载评论
      await loadComments()
    } else {
      ElMessage.error('文章不存在')
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
  } finally {
    loading.value = false
  }
}

// 检查是否已点赞
const hasLiked = ref(false)
const checkHasLiked = async () => {
 // 未登录状态，不获取点赞状态
 if (!isLoggedIn.value) {
 return
 }
 
 try {
   const response = await request({
   url: `/article/liked/${route.params.id}`,
   method: 'get'
   })

  if (response.code === 200 && response.data) {
     hasLiked.value = response.data
   }
  } catch(error) {
   console.error('检查点赞状态失败:', error)
  }
}

// 加载评论列表
const loadComments = async () => {
  try {
    const response = await request({
      url: `/article/list/${route.params.id}`,
      method: 'get',
      params: {
        page: 0,
        size: 100
      }
    })

    if (response.code === 200 && response.data) {
      // 后端返回的是 Page 对象，需要取 content
      comments.value = response.data.content || response.data || []
    }
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

// 提交评论
const submitComment = async () => {
  if (!commentFormRef.value) return

  await commentFormRef.value.validate(async (valid) => {
    if (valid) {
      if (!isLoggedIn.value) {
        ElMessage.warning('请先登录')
        return
      }

      submitting.value = true
      try {
        const response = await request({
          url: '/article/add',
          method: 'post',
          data: {
            articleId: route.params.id,
            content: commentForm.content,
            parentCommentId: commentForm.parentId,
            parentUserName: commentForm.parentUserName
          }
        })

        if (response.code === 200) {
          ElMessage.success('评论成功')
          // 重新加载评论
          await loadComments()
          // 重置表单
          resetForm()
        }
      } catch (error) {
        console.error('评论失败:', error)
        ElMessage.error('评论失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  if (commentFormRef.value) {
    commentFormRef.value.resetFields()
  }
  commentForm.parentId = null
  commentForm.parentUserName = null
}

// 回复评论
const handleReply = (comment) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  commentForm.parentId = comment.id
  commentForm.parentUserName = comment.userName
  // 滚动到表单
  document.querySelector('.comment-form-card')?.scrollIntoView({ behavior: 'smooth' })
}

// 点赞评论
const handleLikeComment = async (comment) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    const response = await request({
      url: `/article/comment/${comment.id}/like`,
      method: 'post'
    })

    if (response.code === 200) {
      ElMessage.success('点赞成功')
      comment.likeCount = (comment.likeCount || 0) + 1
    }
  } catch (error) {
    console.error('点赞评论失败:', error)
    ElMessage.error('点赞评论失败')
  }
}

// 点赞文章
const handleLike = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }

  liking.value = true
  try {
    const response = await request({
      url: `/article/${route.params.id}/like`,
      method: 'post'
    })

    if (response.code === 200) {
      // 切换点赞状态
      hasLiked.value = !hasLiked.value
      if (hasLiked.value) {
        article.value.likeCount = (article.value.likeCount || 0) + 1
        ElMessage.success('点赞成功')
      } else {
        article.value.likeCount = Math.max(0, (article.value.likeCount || 0) - 1)
        ElMessage.info('已取消点赞')
      }
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败')
  } finally {
    liking.value = false
  }
}

// 滚动到评论区
const scrollToComments = () => {
  document.querySelector('.comments-section')?.scrollIntoView({ behavior: 'smooth' })
}

// 分享文章
const handleShare = async () => {
  const shareUrl = window.location.href

  try {
    // 尝试使用 Clipboard API
    if (navigator.clipboard) {
      await navigator.clipboard.writeText(shareUrl)
      ElMessage.success('已复制链接')
    } else {
      // 降级方案：创建临时输入框
      const input = document.createElement('input')
      input.value = shareUrl
      document.body.appendChild(input)
      input.select()
      document.execCommand('copy')
      document.body.removeChild(input)
      ElMessage.success('已复制链接')
    }
  } catch (error) {
    console.error('分享失败:', error)
    ElMessage.error('复制失败')
  }
}

// 检查登录状态
const checkLoginStatus = () => {
  isLoggedIn.value = checkLoggedIn()
  if (isLoggedIn.value) {
    userInfo.value = getUserInfo()
    isAdmin.value = hasRole('admin')
  }
}

// 页面卸载时恢复默认标题
onUnmounted(() => {
  document.title = '个人博客'
})

onMounted(() => {
  checkLoginStatus()
  loadArticle()
})
</script>

<style scoped>
.article-detail-container {
  min-height: 100vh;
  background-color: #f0f2f5;
  background-image:
    linear-gradient(to right, rgba(0, 0, 0, 0.05) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(0, 0, 0, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  background-attachment: fixed;
}

.main-content {
  padding: 40px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.article-wrapper {
  background: transparent;
}

.article-content-wrapper {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 40px;
  margin-bottom: 20px;
}

/* 返回按钮容器 */
.back-button-container {
  padding: 16px 0;
  margin-bottom: 8px;
}

.article-header-section {
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 2px solid #f0f2f5;
}

.article-title {
  font-size: 32px;
  color: #303133;
  margin: 0 0 20px 0;
  font-weight: 600;
  line-height: 1.4;
}

.article-meta {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 20px;
}

/* 左侧悬浮按钮 */
.side-floating-buttons {
  position: fixed;
  left: calc(50% - 650px);
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
  z-index: 100;
}

.floating-btn {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  background: #fff;
  border: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.floating-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  background: #f5f7fa;
}

/* 点赞状态 */
.floating-btn.liked {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8787 100%);
  border-color: #ff6b6b;
}

.floating-btn.liked .el-icon {
  color: #fff;
}

.floating-btn.liked:hover {
  background: linear-gradient(135deg, #ff5252 0%, #ff6b6b 100%);
  box-shadow: 0 6px 16px rgba(255, 107, 107, 0.4);
}

.floating-btn .el-icon {
  font-size: 20px;
  color: #606266;
}

.floating-btn:active .el-icon {
  color: #409EFF;
}

.author-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.author-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.meta-details {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  font-size: 13px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-divider {
  color: #dcdfe6;
  margin: 0 4px;
}

.article-actions-right {
  display: flex;
  gap: 10px;
}

/* 温馨提示 */
.warm-tip {
  background: #fdf6ec;
  border-left: 4px solid #e6a23c;
  border-radius: 4px;
  padding: 12px 16px;
  margin-bottom: 30px;
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: #e6a23c;
  font-size: 13px;
  line-height: 1.6;
}

.warm-tip .el-icon {
  font-size: 16px;
  flex-shrink: 0;
  margin-top: 2px;
}

/* 文章内容 */
.article-content {
  font-size: 16px;
  line-height: 1.8;
  color: #303133;
  padding: 20px 0;
}

/* 段落样式 */
.article-content :deep(p) {
  margin: 16px 0;
  line-height: 1.8;
}

/* 换行符样式 */
.article-content :deep(br) {
  display: block;
  content: "";
  margin: 8px 0;
}

/* 代码块样式 */
.article-content :deep(pre) {
  margin: 20px 0;
  padding: 20px;
  background: #282c34;
  color: #abb2bf;
  border-radius: 6px;
  overflow-x: auto;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 14px;
  line-height: 1.6;
}

.article-content :deep(pre code) {
  background: transparent;
  padding: 0;
  color: #abb2bf;
  font-family: 'Consolas', 'Monaco', monospace;
}

/* 行内代码样式 */
.article-content :deep(code) {
  font-family: 'Consolas', 'Monaco', monospace;
  background: #f5f7fa;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 0.9em;
  color: #e6a23c;
}

.article-content :deep(img) {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 20px auto;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s;
}

.article-content :deep(img:hover) {
  transform: scale(1.02);
}

.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3),
.article-content :deep(h4),
.article-content :deep(h5),
.article-content :deep(h6) {
  margin: 30px 0 16px;
  color: #303133;
  font-weight: 600;
}

.article-content :deep(h1) {
  font-size: 24px;
  border-left: 4px solid #409EFF;
  padding-left: 12px;
}

.article-content :deep(h2) {
  font-size: 20px;
  border-left: 3px solid #409EFF;
  padding-left: 10px;
}

.article-content :deep(h3) {
  font-size: 18px;
  border-left: 2px solid #409EFF;
  padding-left: 8px;
}

.article-content :deep(ul),
.article-content :deep(ol) {
  padding-left: 24px;
  margin: 16px 0;
}

.article-content :deep(li) {
  margin: 8px 0;
}

.article-content :deep(blockquote) {
  margin: 20px 0;
  padding: 16px;
  background: #f5f7fa;
  border-left: 4px solid #409EFF;
  border-radius: 4px;
}

.article-content :deep(a) {
  color: #409EFF;
  text-decoration: none;
  border-bottom: 1px solid #409EFF;
}

.article-content :deep(a:hover) {
  text-decoration: underline;
}

.article-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
}

.article-content :deep(th),
.article-content :deep(td) {
  border: 1px solid #dcdfe6;
  padding: 12px;
  text-align: left;
}

.article-content :deep(th) {
  background: #f5f7fa;
  font-weight: 600;
}

/* 标签 */
.article-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.tag-item {
  font-size: 12px;
  padding: 2px 8px;
}

/* 底部操作按钮 */
.article-actions-bottom {
  display: flex;
  gap: 12px;
  padding-top: 20px;
  margin-top: 30px;
  border-top: 1px solid #f0f2f5;
  justify-content: center;
}

/* 评论区样式 */
.comments-section {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comments-card,
.comment-form-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.comments-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-icon {
  color: #f08080;
  font-size: 18px;
}

.comments-count {
  color: #909399;
  font-size: 14px;
  font-weight: 400;
}

.comments-list {
  min-height: 200px;
}

.no-comments {
  padding: 40px 0;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 20px 0;
  border-bottom: 1px solid #f0f2f5;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comment-author {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.comment-time {
  font-size: 13px;
  color: #909399;
}

.comment-text {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  margin-bottom: 12px;
}

.comment-actions {
  display: flex;
  gap: 10px;
}

/* 回复列表 */
.replies-list {
  margin-top: 15px;
  padding-left: 55px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.reply-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.reply-avatar {
  flex-shrink: 0;
}

.reply-content {
  flex: 1;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reply-author {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.reply-time {
  font-size: 12px;
  color: #909399;
}

.reply-text {
  font-size: 13px;
  line-height: 1.6;
  color: #606266;
  margin-bottom: 8px;
}

.reply-to {
  color: #409EFF;
  font-weight: 500;
}

.reply-actions {
  display: flex;
  gap: 10px;
}

/* 评论表单 */
.form-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.comment-form {
  padding: 10px 0;
}

.comment-form :deep(.el-form-item) {
  margin-bottom: 10px;
}

.comment-form :deep(.el-textarea__inner) {
  border-radius: 8px;
}

@media (max-width: 768px) {
  .main-content {
    padding: 20px 10px;
  }

  .article-content-wrapper {
    padding: 20px;
  }

  .article-title {
    font-size: 24px;
  }

  .article-content {
    font-size: 14px;
  }

  .article-meta {
    flex-direction: column;
    gap: 15px;
  }

  .article-actions-right {
    width: 100%;
    justify-content: flex-end;
  }

  .comment-item {
    flex-direction: column;
  }

  .replies-list {
    padding-left: 20px;
  }

  .reply-item {
    flex-direction: column;
  }
}
</style>
