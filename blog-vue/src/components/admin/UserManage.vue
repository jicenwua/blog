<template>
  <div class="user-manage-content">
    <el-card class="user-manage-card">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon"><User /></el-icon>
          <span>用户管理</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchForm.username"
          placeholder="搜索用户邮箱"
          clearable
          style="width: 200px;"
          @keyup.enter="handleSearch"
        />
        <el-input
          v-model="searchForm.name"
          placeholder="搜索用户名称"
          clearable
          style="width: 250px; margin-left: 10px;"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch" style="margin-left: 10px;">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <!-- 用户列表 -->
      <el-table
        :data="userList"
        style="width: 100%; height: calc(100vh - 280px);"
        border
        v-loading="loading"
        :max-height="'100%'"
      >
        <el-table-column prop="userId" label="用户 ID" min-width="100" />
        <el-table-column prop="username" label="用户邮箱" min-width="150" />
        <el-table-column prop="name" label="昵称" min-width="120" />
        <el-table-column label="角色" min-width="120">
          <template #default="{ row }">
            <el-tag :type="row.roles?.includes('ADMIN') ? 'danger' : 'primary'">
              {{ row.roles?.includes('ADMIN') ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'">
              {{ row.enabled ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userLoginTime" label="最后登录时间" min-width="180" />
        <el-table-column label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              :type="row.enabled ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.enabled ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editFormRules"
        label-width="80px"
      >
        <el-form-item label="用户邮箱">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="editForm.name" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="editForm.roles">
            <el-radio label="USER">普通用户</el-radio>
            <el-radio label="ADMIN">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input
            v-model="editForm.password"
            type="password"
            placeholder="不修改请留空"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUser } from '@/api/user'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('编辑用户')
const editFormRef = ref(null)

const searchForm = reactive({
  username: '',
  name: ''  // 修改为 name，对应后端的 name 字段
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 用户列表数据
const userList = ref([])

const editForm = reactive({
  userId: null,
  username: '',
  name: '',
  roles: 'USER',
  password: '',
  enabled: true  // 用户状态，默认为启用
})

const editFormRules = {
  name: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ]
}

// 加载用户列表
const loadUserList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      username: searchForm.username,
      name: searchForm.name
    }
    
    const res = await getUserList(params)
    
    if (res.code === 200 && res.rows) {
      userList.value = res.rows.map(user => ({
        userId: user.userId,
        username: user.userUsername,
        name: user.userName,
        roles: [user.userRole === 1 ? 'ADMIN' : 'USER'],  // 1=管理员，2=普通用户
        enabled: user.userType,
        userLoginTime: user.userLoginTime
      }))
      pagination.total = res.total || 0
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1  // 重置到第一页
  loadUserList()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.name = ''
  pagination.pageNum = 1
  loadUserList()
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  dialogVisible.value = true
  editForm.userId = row.userId
  editForm.username = row.username
  editForm.name = row.name
  editForm.roles = row.roles?.[0] || 'USER'
  editForm.password = ''  // 密码默认为空
  editForm.enabled = row.enabled  // 保存用户当前状态
}

const handleDialogClose = () => {
  editFormRef.value?.resetFields()
}

const handleEditSubmit = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 构建更新数据
        const updateData = {
          userId: editForm.userId,
          userName: editForm.name,
          userRole: editForm.roles === 'ADMIN' ? 1 : 2,  // 1=管理员，2=普通用户
          userType: editForm.enabled  // 保持用户当前状态
        }
        
        // 如果填写了密码，则更新密码
        if (editForm.password && editForm.password.trim()) {
          updateData.userPassword = editForm.password
        }
        
        const res = await updateUser(updateData)
        
        if (res.code === 200) {
          ElMessage.success('更新成功')
          dialogVisible.value = false
          loadUserList()  // 重新加载用户列表
        } else {
          ElMessage.error(res.message || '更新失败')
        }
      } catch (error) {
        console.error('更新用户失败:', error)
        ElMessage.error(error.message || '更新失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleToggleStatus = (row) => {
  const action = row.enabled ? '禁用' : '启用'
  ElMessageBox.confirm(
    `确定要${action}该用户吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 构建更新数据
      const updateData = {
        userId: row.userId,
        userType: !row.enabled  // 切换状态
      }
      
      const res = await updateUser(updateData)
      
      if (res.code === 200) {
        ElMessage.success(`${action}成功`)
        loadUserList()  // 重新加载用户列表
      } else {
        ElMessage.error(res.message || `${action}失败`)
      }
    } catch (error) {
      console.error(`${action}用户失败:`, error)
      ElMessage.error(error.message || `${action}失败`)
    }
  }).catch(() => {
    // 用户取消
  })
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadUserList()
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  loadUserList()
}

onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.user-manage-content {
  padding: 20px;
  height: calc(100vh - 120px); /* 减去顶部导航和侧边栏的高度 */
  overflow: hidden;
}

.user-manage-card {
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

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  flex-shrink: 0; /* 防止搜索栏被压缩 */
}

/* 表格容器占满剩余空间 */
:deep(.el-card__body) {
  flex: 1;
  overflow: auto;
  display: flex;
  flex-direction: column;
}

/* 分页固定在底部 */
:deep(.el-pagination) {
  margin-top: 20px;
  justify-content: flex-end;
  flex-shrink: 0;
}
</style>
