import { ref } from 'vue'

// 用户信息
const userInfo = ref(null)

// Token
const token = ref(localStorage.getItem('token') || null)

// 是否记住我
const rememberMe = ref(localStorage.getItem('rememberMe') === 'true')

/**
 * 设置用户信息
 */
export const setUserInfo = (info) => {
  userInfo.value = info
  if (info) {
    localStorage.setItem('userInfo', JSON.stringify(info))
  } else {
    localStorage.removeItem('userInfo')
  }
}

/**
 * 获取用户信息
 */
export const getUserInfo = () => {
  if (!userInfo.value) {
    const stored = localStorage.getItem('userInfo')
    if (stored) {
      userInfo.value = JSON.parse(stored)
    }
  }
  return userInfo.value
}

/**
 * 设置 Token
 */
export const setToken = (newToken, isRememberMe = false) => {
  token.value = newToken
  if (isRememberMe) {
    localStorage.setItem('token', newToken)
    localStorage.setItem('rememberMe', 'true')
  } else {
    sessionStorage.setItem('token', newToken)
    localStorage.removeItem('token')
    localStorage.removeItem('rememberMe')
  }
}

/**
 * 获取 Token
 */
export const getToken = () => {
  if (!token.value) {
    // 优先从 session 存储获取（未勾选记住我）
    token.value = sessionStorage.getItem('token')
  }
  if (!token.value) {
    // 其次从 local 存储获取（勾选记住我）
    token.value = localStorage.getItem('token')
  }
  return token.value
}

/**
 * 检查是否已登录
 */
export const isLoggedIn = () => {
  return !!getToken() && !!getUserInfo()
}

/**
 * 检查用户是否有指定角色
 * @param {string} role - 角色名称 (e.g., 'admin')
 */
export const hasRole = (role) => {
  const info = getUserInfo()
  if (!info || !info.roles) {
    return false
  }
  // 将权限值转换为大写进行匹配（后端返回的是大写，如 "ADMIN"）
  const targetRole = role.toUpperCase()
  return info.roles.some(r => r.toUpperCase() === targetRole)
}

/**
 * 退出登录
 */
export const logout = async () => {
  try {
    // 先调用后端退出登录接口
    const { logout: logoutApi } = await import('@/api/user')
    await logoutApi()
  } catch (error) {
    // 即使接口调用失败，也清除本地状态
  } finally {
    // 清除本地存储
    token.value = null
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('rememberMe')
    sessionStorage.removeItem('token')
  }
}

/**
 * 初始化用户状态（应用启动时调用）
 */
export const initUserState = () => {
  getUserInfo()
  getToken()
}
