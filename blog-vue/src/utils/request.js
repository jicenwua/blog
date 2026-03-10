import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || 'http://localhost:8080', // 网关地址
  timeout: import.meta.env.VITE_APP_TIMEOUT || 10000, // 请求超时时间
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 在请求路径前添加 /xcz/blog
    config.url = `/xcz/blog${config.url}`

    // 如果有 token，添加到请求头
    // 优先从 localStorage 获取（记住我），否则从 sessionStorage 获取
    let token = localStorage.getItem('token')
    if (!token) {
      token = sessionStorage.getItem('token')
    }

    if (token) {
      // 使用标准方式设置 Authorization 头
      config.headers.set('Authorization', `Bearer ${token}`)
    }

    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    const httpStatus = response.status

    // 如果返回的状态码不是 200，说明接口有错误
    if (res.code !== 200) {
      // 显示后端返回的错误信息和错误码
      const errorMsg = res.msg || res.message || '请求失败'
      const errorCode = res.code

      // 如果是 token 过期或无效，跳转到登录页（不显示错误提示）
      if (res.code === 401 || res.code === 403 || httpStatus === 401 || httpStatus === 403) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        sessionStorage.removeItem('token')
        // 使用 replace 避免用户点击返回按钮回到原页面
        window.location.replace('/login')
        return Promise.reject(new Error(errorMsg))
      }

      ElMessage.error(`[${errorCode}] ${errorMsg}`)
      return Promise.reject(new Error(errorMsg))
    }

    return res
  },
  error => {
    if (error.response) {
      const httpStatus = error.response.status
      const responseData = error.response.data

      // HTTP 状态码错误
      switch (httpStatus) {
        case 401:
          // Token 失效，清除所有用户数据并跳转到登录页
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          sessionStorage.removeItem('token')
          // 使用 replace 避免用户点击返回按钮回到原页面
          window.location.replace('/login')
          break
        case 403:
          const forbiddenMsg = responseData?.msg || responseData?.message || '拒绝访问'
          ElMessage.error(`[403] ${forbiddenMsg}`)
          break
        case 404:
          const notFoundMsg = responseData?.msg || responseData?.message || '请求地址不存在'
          ElMessage.error(`[404] ${notFoundMsg}`)
          break
        case 500:
          const serverErrorMsg = responseData?.msg || responseData?.message || '服务器内部错误'
          ElMessage.error(`[500] ${serverErrorMsg}`)
          break
        default:
          // 尝试从响应数据中获取错误信息
          const errorMsg = responseData?.msg || responseData?.message || '请求失败'
          const errorCode = responseData?.code || httpStatus
          ElMessage.error(`[${errorCode}] ${errorMsg}`)
      }
    } else if (error.request) {
      // 只有在非 401 情况下才显示网络错误提示
      if (!error.response || error.response.status !== 401) {
        ElMessage.error('网络错误，请检查网络连接')
      }
    } else {
      // 其他错误（包括业务异常）
      if (error.message && !error.message.includes('请求失败')) {
        ElMessage.error(error.message)
      } else {
        ElMessage.error('请求失败')
      }
    }

    return Promise.reject(error)
  }
)

export default request
