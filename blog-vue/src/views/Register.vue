<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>欢迎注册</h2>
        <p>加入个人博客</p>
      </div>

      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="register-form">
        <el-form-item prop="userName">
          <el-input
            v-model="registerForm.userName"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
            clearable
          />
        </el-form-item>

        <el-form-item prop="userUsername">
          <el-input
            v-model="registerForm.userUsername"
            placeholder="请输入邮箱"
            :prefix-icon="Message"
            size="large"
            clearable
          />
        </el-form-item>

        <el-form-item prop="userPassword">
          <el-input
            v-model="registerForm.userPassword"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleRegister"
            class="register-btn"
          >
            注 册
          </el-button>
        </el-form-item>

        <div class="login-link">
          已有账号？
          <router-link to="/login" class="login-btn">立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { register, login } from '@/api/user'
import { setToken, setUserInfo, getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

// 注册表单数据
const registerForm = reactive({
  userName: '',           // 用户名
  userUsername: '',       // 邮箱（用户账号）
  userPassword: '',       // 密码
  confirmPassword: '',    // 确认密码
  agreement: false
})

// 验证密码一致性
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.userPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules = {
  userName: [  // 改为 userName
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  userUsername: [  // 改为 userUsername
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  userPassword: [  // 改为 userPassword
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agreement: [
    { validator: (rule, value, callback) => {
      if (!value) {
        callback(new Error('请同意用户协议'))
      } else {
        callback()
      }
    }, trigger: 'change' }
  ]
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true

      try {
        // 调用后端注册接口
        const res = await register({
          userName: registerForm.userName,
          userUsername: registerForm.userUsername,
          userPassword: registerForm.userPassword
        })

        // 注册成功
        if (res.code === 200) {
          ElMessage.success('注册成功，正在自动登录...')

          // 自动登录
          try {
            const loginRes = await login({
              userUsername: registerForm.userUsername,
              userPassword: registerForm.userPassword,
              rememberMe: true
            })

            if (loginRes.data && loginRes.data.token) {
              // 保存 token 和用户信息
              setToken(loginRes.data.token, true)
              setUserInfo({
                userName: registerForm.userName,
                userUsername: registerForm.userUsername
              })

              ElMessage.success('登录成功，即将跳转首页')

              // 跳转到首页
              setTimeout(() => {
                router.push('/')
              }, 1000)
            }
          } catch (loginError) {
            console.error('自动登录失败:', loginError)
            ElMessage.warning('注册成功，请登录')
            router.push('/login')
          }
        }
      } catch (error) {
        console.error('注册失败:', error)
        // 错误已在 request.js 中处理，这里不需要再次显示
      } finally {
        loading.value = false
      }
    }
  })
}

import { onMounted } from 'vue'

onMounted(() => {
  // 落花特效已在 App.vue 中全局挂载
})
</script>

<style scoped>
.register-container {
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

.register-box {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
  font-weight: 600;
}

.register-header p {
  font-size: 14px;
  color: #909399;
}

.register-form {
  margin-top: 20px;
}

.agreement-link {
  color: #409EFF;
  text-decoration: none;
}

.agreement-link:hover {
  color: #66b1ff;
}

.register-btn {
  width: 100%;
  font-size: 16px;
  font-weight: 500;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #606266;
}

.login-btn {
  color: #409EFF;
  text-decoration: none;
  font-weight: 500;
  margin-left: 5px;
}

.login-btn:hover {
  color: #66b1ff;
}

/* 响应式 */
@media (max-width: 768px) {
  .register-box {
    width: 90%;
    padding: 30px 20px;
  }
}
</style>
