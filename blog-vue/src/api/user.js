import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - 登录信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {boolean} data.rememberMe - 是否记住我
 * @returns {Promise}
 */
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * @param {Object} data - 注册信息
 * @param {string} data.username - 用户名
 * @param {string} data.email - 邮箱
 * @param {string} data.password - 密码
 * @returns {Promise}
 */
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

/**
 * 获取用户信息
 * @returns {Promise}
 */
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get',
    params: {
      pageNum: 1,
      pageSize: 1
    }
  })
}

/**
 * 验证 Token
 * @returns {Promise}
 */
export function validateTokenApi() {
  return request({
    url: '/user/validate',
    method: 'post',
    headers: {
      'Accept': 'application/json'
    }
  })
}

/**
 * 退出登录
 * @returns {Promise}
 */
export function logout() {
  return request({
    url: '/user/logout',
    method: 'post',
    // 不发送请求体，避免后端解析失败
    data: null
  })
}

/**
 * 查询用户列表（管理员使用）
 * @param {Object} data - 查询参数
 * @param {number} data.pageNum - 页码
 * @param {number} data.pageSize - 每页数量
 * @param {string} data.username - 用户名（可选）
 * @param {string} data.name - 用户名称（可选）
 * @returns {Promise}
 */
export function getUserList(data) {
  return request({
    url: '/user/info',
    method: 'post',
    data
  })
}

/**
 * 更新用户信息（管理员使用）
 * @param {Object} data - 用户信息
 * @returns {Promise}
 */
export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  })
}
