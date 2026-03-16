import request from '@/utils/request'

/**
 * 发布文章
 * @param {Object} data - 文章信息
 * @param {string} data.title - 标题
 * @param {string} data.content - 内容
 * @param {string} data.category - 分类
 * @param {Array} data.tags - 标签
 * @returns {Promise}
 */
export function publishArticle(data) {
  return request({
    url: '/article/publish',
    method: 'post',
    data
  })
}

/**
 * 获取文章详情
 * @param {string} id - 文章 ID
 * @returns {Promise}
 */
export function getArticleById(id) {
 return request({
   url: `/article/xq/${id}`,
   method: 'get'
  })
}

/**
 * 获取文章列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getArticleList(params) {
  return request({
    url: '/article/list',
    method: 'get',
    params
  })
}

/**
 * 点赞文章
 * @param {string} id - 文章 ID
 * @returns {Promise}
 */
export function likeArticle(id) {
 return request({
   url: `/article/like/${id}`,
   method: 'post'
  })
}

/**
 * 检查用户是否已点赞文章
 * @param {string} id - 文章 ID
 * @returns {Promise}
 */
export function checkArticleLiked(id) {
 return request({
   url: `/article/liked/${id}`,
   method: 'get'
  })
}

/**
 * 点赞评论
 * @param {string} commentId - 评论 ID
 * @returns {Promise}
 */
export function likeComment(commentId) {
 return request({
   url: `/article/comment/${commentId}/like`,
   method: 'post'
  })
}

/**
 * 检查用户是否已点赞评论
 * @param {string} commentId - 评论 ID
 * @returns {Promise}
 */
export function checkCommentLiked(commentId) {
 return request({
   url: `/article/comment/${commentId}/liked`,
   method: 'get'
  })
}

/**
 * 增加浏览次数
 * @param {string} id - 文章 ID
 * @returns {Promise}
 */
export function incrementViewCount(id) {
 return request({
   url: `/article/view/${id}`,
   method: 'post'
  })
}

/**
 * 上传图片
 * @param {File} file - 图片文件
 * @returns {Promise}
 */
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/article/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取当前用户的文章列表
 * @returns {Promise}
 */
export function getMyArticles() {
  return request({
    url: '/article/my/list',
    method: 'get'
  })
}

/**
 * 删除文章
 * @param {string} id - 文章 ID
 * @returns {Promise}
 */
export function deleteArticle(id) {
  return request({
    url: `/article/${id}`,
    method: 'delete'
  })
}

/**
 * 更新文章
 * @param {string} id - 文章 ID
 * @param {Object} data - 文章信息
 * @param {string} data.title - 标题
 * @param {string} data.content - 内容
 * @param {string} data.categoryId - 分类 ID
 * @param {string} data.categoryName - 分类名称
 * @param {Array} data.tags - 标签
 * @returns {Promise}
 */
export function updateArticle(id, data) {
  return request({
    url: `/article/${id}`,
    method: 'put',
    data
  })
}

/**
 * 获取统计数据（文章总数、总浏览量、总评论数）
 * @returns {Promise}
 */
export function getStatistics() {
  return request({
    url: '/article/statistics',
    method: 'get'
  })
}

/**
 * 获取分类统计
 * @returns {Promise}
 */
export function getCategoryStatistics() {
  return request({
    url: '/article/categories/stats',
    method: 'get'
  })
}

/**
 * 搜索文章
 * @param {Object} params - 搜索参数
 * @param {string} params.keyword - 搜索关键词
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页数量
 * @returns {Promise}
 */
export function searchArticles(params) {
  return request({
    url: '/article/search',
    method: 'get',
    params
  })
}

/**
 * 获取热门标签
 * @param {number} limit - 返回数量限制
 * @returns {Promise}
 */
export function getHotTags(limit = 10) {
  return request({
    url: '/article/tags/hot',
    method: 'get',
    params: { limit }
  })
}
