<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>欢迎登录</h2>
        <p>个人博客</p>
      </div>

      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
        <el-form-item prop="userUsername">
          <el-input
            v-model="loginForm.userUsername"
            placeholder="请输入邮箱"
            :prefix-icon="User"
            size="large"
            clearable
          />
        </el-form-item>

        <el-form-item prop="userPassword">
          <el-input
            v-model="loginForm.userPassword"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
          <router-link to="/forgot-password" class="forgot-password">忘记密码？</router-link>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            登 录
          </el-button>
        </el-form-item>

        <div class="register-link">
          还没有账号？
          <router-link to="/register" class="register-btn">立即注册</router-link>
        </div>

      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/user'
import { setToken, setUserInfo, getUserInfo } from '@/utils/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  userUsername: '',     // 邮箱账号
  userPassword: '',     // 密码
  rememberMe: false
})

// 页面加载时检查是否有记住的 token
onMounted(() => {
  const userInfo = getUserInfo()
  if (userInfo) {
    // 如果之前保存了用户信息，填充用户名（邮箱）
    loginForm.userUsername = userInfo.userUsername || ''
    loginForm.rememberMe = true
  }
})

// 表单验证规则
const loginRules = {
  userUsername: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  userPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      
      try {
        // 调用后端登录接口
        const res = await login({
          userUsername: loginForm.userUsername,
          userPassword: loginForm.userPassword,
          rememberMe: loginForm.rememberMe
        })
        
        // 登录成功，保存 token 和用户信息
        if (res.data && res.data.token) {
          // 根据记住我选项决定存储方式
          setToken(res.data.token, loginForm.rememberMe)
          
          // 保存用户信息（从登录表单获取）
          setUserInfo({
            userName: loginForm.userUsername.split('@')[0], // 如果是邮箱，取@前部分作为昵称
            userUsername: loginForm.userUsername,
            roles: res.data.permissions || [] // 保存权限列表（后端返回的是 permissions）
          })
          
          ElMessage.success('登录成功')
          
          // 跳转到首页
          setTimeout(() => {
            router.push('/')
          }, 500)
        } else {
          ElMessage.error('登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        // 错误已在 request.js 中处理，这里不需要再次显示
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  background-color: #f0f2f5;
  background-image:
    linear-gradient(to right, rgba(0, 0, 0, 0.05) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(0, 0, 0, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  background-attachment: fixed;
  position: relative;
  overflow-x: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
  font-weight: 600;
}

.login-header p {
  font-size: 14px;
  color: #909399;
}

.login-form {
  margin-top: 20px;
}

.forgot-password {
  float: right;
  font-size: 13px;
  color: #409EFF;
  text-decoration: none;
}

.forgot-password:hover {
  color: #66b1ff;
}

.login-btn {
  width: 100%;
  font-size: 16px;
  font-weight: 500;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #606266;
}

.register-btn {
  color: #409EFF;
  text-decoration: none;
  font-weight: 500;
  margin-left: 5px;
}

.register-btn:hover {
  color: #66b1ff;
}

.other-login {
  margin-top: 30px;
}

.other-login .el-divider {
  margin: 20px 0;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.social-btn {
  width: 50px;
  height: 50px;
  border: 1px solid #dcdfe6;
  color: #606266;
}

.social-btn:hover {
  border-color: #409EFF;
  color: #409EFF;
}

/* 响应式 */
@media (max-width: 768px) {
  .login-box {
    width: 90%;
    padding: 30px 20px;
  }
}
</style>
