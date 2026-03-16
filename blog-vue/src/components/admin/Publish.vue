<template>
  <div class="publish-content">
    <el-card class="publish-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button text @click="handleBack">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
          </div>
          <div class="header-center">
            <el-icon class="header-icon"><Edit /></el-icon>
            <span>{{ isEditMode ? '编辑文章' : '发布文章' }}</span>
          </div>
        </div>
      </template>

      <el-form
          ref="formRef"
          :model="articleForm"
          :rules="formRules"
          label-width="100px"
          class="publish-form"
      >
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="articleForm.title" placeholder="请输入文章标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="文章分类" prop="category">
          <el-select v-model="articleForm.category" placeholder="请选择文章分类" style="width: 300px;">
            <el-option label="后端" value="后端" />
            <el-option label="缓存" value="缓存" />
            <el-option label="数据库" value="数据库" />
            <el-option label="消息中间件" value="消息中间件" />
            <el-option label="AI" value="AI" />
          </el-select>
        </el-form-item>

        <el-form-item label="文章标签" prop="tags">
          <div class="tag-input-container">
            <el-tag
                v-for="(tag, index) in articleForm.tags"
                :key="index"
                closable
                :type="getTagType(index)"
                style="margin-right: 8px; margin-bottom: 8px;"
                @close="removeTag(index)"
            >
              {{ tag }}
            </el-tag>
            <template v-if="articleForm.tags.length < 3">
              <el-input
                  v-if="inputVisible"
                  ref="inputRef"
                  v-model="inputValue"
                  size="small"
                  style="width: 120px; display: inline-block;"
                  @blur="handleInputConfirm"
                  @keyup.enter="handleInputConfirm"
              />
              <el-button
                  v-if="!inputVisible"
                  size="small"
                  @click="showInput"
              >
                + 添加标签
              </el-button>
            </template>
            <span v-else class="tag-limit-tip">最多 3 个标签</span>
          </div>
        </el-form-item>

        <el-form-item label="文章内容" prop="content" class="content-form-item">
          <div class="editor-container">
            <Toolbar
                style="border-bottom: 1px solid #ccc"
                :editor="editorRef"
                :defaultConfig="toolbarConfig"
                mode="default"
            />
            <Editor
                style="flex: 1; overflow-y: auto;"
                v-model="articleForm.content"
                :defaultConfig="editorConfig"
                mode="default"
                @onCreated="handleCreated"
            />
          </div>
        </el-form-item>
      </el-form>

      <div class="form-footer">
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEditMode ? '保存修改' : '立即发布' }}
        </el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button v-if="isEditMode" @click="handleCancel">取消</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onBeforeUnmount, shallowRef, onMounted } from 'vue'
import { Edit, ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { publishArticle, getArticleById, updateArticle } from '@/api/article'
import { useRouter, useRoute } from 'vue-router'
import { compressImage, blobToFile, formatFileSize } from '@/utils/imageCompress'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const submitting = ref(false)
const editorRef = shallowRef()
const isEditMode = ref(false)
const articleId = ref('')

const articleForm = reactive({
  title: '',
  category: '',
  tags: [],
  content: ''
})

const formRules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择文章分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const inputVisible = ref(false)
const inputRef = ref(null)
const inputValue = ref('')

const toolbarConfig = {}
const editorConfig = {
  placeholder: '请输入内容...',
  // 隐藏视频菜单
  excludeKeys: ['group-video'],
  // 粘贴配置 - 增强样式保留
  onPaste: {
    // 保留更多样式
    keepInlineStyles: true,
    keepPre: true,
    keepCodeBlock: true,
    // 不过滤样式
    filterStyle: false,
    // 允许更多 HTML 标签
    allowedStyles: {
      '*': {
        'color': /.+/,
        'background-color': /.+/,
        'font-size': /.+/,
        'font-weight': /.+/,
        'font-style': /.+/,
        'text-decoration': /.+/,
        'text-align': /.+/,
        'line-height': /.+/,
        'margin': /.+/,
        'padding': /.+/,
        'border': /.+/,
        'width': /.+/,
        'height': /.+/,
        'display': /.+/,
        'position': /.+/,
        'top': /.+/,
        'left': /.+/,
        'right': /.+/,
        'bottom': /.+/
      }
    }
  },
  MENU_CONF: {
    // 图片上传配置
    uploadImage: {
      // 服务端地址 - 需要添加网关前缀和路由前缀
      server: import.meta.env.VITE_APP_BASE_API + '/xcz/blog/article/upload/image',
      // 文件最大体积 (5MB)
      maxFileSize: 5 * 1024 * 1024,
      // 最多可上传多少个文件
      maxNumberOfFiles: 100,
      // 文件类型
      allowedFileTypes: ['image/*'],
      // 自定义 headers - 在这里添加 Token
      headers: {
        'Authorization': localStorage.getItem('token') || ''
      },
      // meta 参数（如果需要传递其他参数）
      meta: {},
      // 指定上传文件的字段名，必须与后端 @RequestParam 一致
      fieldName: 'file',
      // 单个文件的最大体积
      maxFileSizeForImage: 5 * 1024 * 1024,

      // ⭐ 自定义上传前处理：先缓存到本地，发布时再上传
      async customUpload(file, insertFn) {
        try {
          // 压缩图片
          const compressedBlob = await compressImage(file, {
            maxWidth: 1920,
            maxHeight: 1080,
            quality: 0.8,
            maxSize: 500 * 1024, // 500KB
            outputFormat: 'image/jpeg'
          })

          console.log('压缩后大小:', compressedBlob.size)

          // 转换为 File 对象
          const compressedFile = blobToFile(compressedBlob, file.name.replace(/\.[^.]+$/, '.jpg'))

          // 生成临时 URL 用于显示
          const tempUrl = URL.createObjectURL(compressedFile)

          // 将图片信息缓存到临时数组中
          if (!window.tempImageCache) {
            window.tempImageCache = []
          }
          window.tempImageCache.push({
            file: compressedFile,
            tempUrl: tempUrl,
            id: Date.now() // 唯一 ID
          })

          // 立即插入到编辑器（使用临时 URL）
          insertFn(tempUrl, '', {})
        } catch (error) {
          console.error('图片处理失败:', error)
          ElMessage.error('图片处理失败：' + (error.message || '未知错误'))
        }
      },
      // 自定义插入图片
      customInsert(res, insertFn) {
        // res 是服务端返回的数据
        // insertFn 是 wangeditor 内部的方法，用来插入图片
        if (res.code === 200 && res.data) {
          insertFn(res.data.url, res.data.alt || '', {})
        } else {
          ElMessage.error(res.message || '图片上传失败')
        }
      }
    }
  }
}

const handleCreated = (editor) => {
  editorRef.value = editor

  editor.on('paste', (event) => {
    const clipboardData = event.clipboardData || event.originalEvent.clipboardData

    if (clipboardData && clipboardData.items) {
      const items = clipboardData.items

      for (let i = 0; i < items.length; i++) {
        const item = items[i]

        // 处理图片
        if (item.type.indexOf('image') !== -1) {
          const file = item.getAsFile()
          if (file) {
            event.preventDefault()
            handlePasteImage(file, editor)
            return
          }
        }

        // 处理 HTML 内容 - 保留样式但处理序号
        if (item.type === 'text/html') {
          item.getAsString((html) => {
            if (html) {
              event.preventDefault()
              // 清理并转换 HTML，避免序号冲突
              const cleanedHtml = cleanPasteHtml(html)
              // 使用 insertText 配合 HTML 插入，避免重新计算序号
              insertCleanHtml(editor, cleanedHtml)
            }
          })
        }
      }
    }
  })
}

// 清理粘贴的 HTML，移除可能导致序号冲突的内容
const cleanPasteHtml = (html) => {
  if (!html) return ''

  // 创建临时 DOM 元素
  const tempDiv = document.createElement('div')
  tempDiv.innerHTML = html

  // 移除所有有序列表的自动编号样式
  const elements = tempDiv.querySelectorAll('*')
  elements.forEach(el => {
    // 移除可能影响序号的内联样式
    if (el.style) {
      el.style.removeProperty('list-style-type')
      el.style.removeProperty('counter-reset')
      el.style.removeProperty('counter-increment')
    }
    // 移除 class 中的序号相关类
    if (el.classList) {
      const classesToRemove = ['numbered-list', 'ordered-list-item', 'list-number']
      classesToRemove.forEach(cls => el.classList.remove(cls))
    }
  })

  return tempDiv.innerHTML
}

// 插入清理后的 HTML，避免序号冲突
const insertCleanHtml = (editor, html) => {
  try {
    // 获取当前选区
    const selection = editor.getSelection()
    if (selection) {
      // 将 HTML 转换为文本节点插入，避免重新解析序号
      const tempDiv = document.createElement('div')
      tempDiv.innerHTML = html

      // 逐个子节点插入，保持样式但避免序号冲突
      const nodes = Array.from(tempDiv.childNodes)
      nodes.forEach(node => {
        if (node.nodeType === 1) { // 元素节点
          // 对于列表项，转换为普通段落
          if (node.tagName === 'LI' || node.tagName === 'OL' || node.tagName === 'UL') {
            const p = document.createElement('p')
            p.innerHTML = node.innerHTML
            editor.insertNode(p)
          } else {
            editor.insertNode(node)
          }
        } else if (node.nodeType === 3) { // 文本节点
          editor.insertText(node.textContent)
        }
      })
    }
  } catch (error) {
    console.error('插入 HTML 失败:', error)
    // 降级方案：直接插入文本
    editor.insertText(html.replace(/<[^>]*>/g, ''))
  }
}

// 处理粘贴的图片
const handlePasteImage = async (file, editor) => {
  try {
    // 压缩图片
    const compressedBlob = await compressImage(file, {
      maxWidth: 1920,
      maxHeight: 1080,
      quality: 0.8,
      maxSize: 500 * 1024, // 500KB
      outputFormat: 'image/jpeg'
    })

    // 转换为 File 对象
    const compressedFile = blobToFile(compressedBlob, file.name.replace(/\.[^.]+$/, '.jpg'))

    // 生成临时 URL 用于显示
    const tempUrl = URL.createObjectURL(compressedFile)

    // 将图片信息缓存到临时数组中
    if (!window.tempImageCache) {
      window.tempImageCache = []
    }
    window.tempImageCache.push({
      file: compressedFile,
      tempUrl: tempUrl,
      id: Date.now()
    })

    // 插入图片到编辑器
    editor.insertImage(tempUrl, '', {})
  } catch (error) {
    console.error('粘贴图片处理失败:', error)
    ElMessage.error('图片处理失败')
  }
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
  // 清理临时图片缓存
  clearTempImageCache()
})

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 获取编辑器内容，检查是否为 HTML
        const editor = editorRef.value
        let htmlContent = editor ? editor.getHtml() : articleForm.content

        // 获取来源页面信息
        const fromPage = route.query.from || '/statistics'
        const menuIndex = route.query.menu || '2'

        // ⭐ 发布文章时，先上传所有缓存的图片
        let uploadedImageUrls = []
        if (window.tempImageCache && window.tempImageCache.length > 0) {
          uploadedImageUrls = await uploadCachedImages()

          // 替换编辑器中的临时 URL 为正式 URL
          if (uploadedImageUrls.length > 0 && editor) {
            replaceImageUrlsInEditor(editor, uploadedImageUrls)
            // 重新获取更新后的 HTML 内容
            const updatedHtmlContent = editor.getHtml()
            // 使用更新后的内容
            htmlContent = updatedHtmlContent
          }
        }

        if (isEditMode.value) {
          // 更新文章
          const res = await updateArticle(articleId.value, {
            title: articleForm.title,
            content: htmlContent, // 使用编辑器的 HTML 内容
            categoryId: articleForm.category,
            categoryName: articleForm.category,
            tags: articleForm.tags
          })

          if (res.code === 200) {
            ElMessage.success('更新成功')
            // 返回来源页面
            router.push(fromPage)
            // 如果需要切换到特定菜单，可以在 Statistics 组件中处理
          }
        } else {
          // 发布文章
          const res = await publishArticle({
            title: articleForm.title,
            content: htmlContent, // 使用编辑器的 HTML 内容
            categoryId: articleForm.category,
            categoryName: articleForm.category,
            tags: articleForm.tags
          })

          if (res.code === 200) {
            ElMessage.success('发布成功')
            // 重置表单
            handleReset()
            // 返回来源页面
            router.push(fromPage)
          }
        }
      } catch (error) {
        ElMessage.error(isEditMode.value ? '更新失败' : '发布失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// ⭐ 上传缓存的图片
const uploadCachedImages = async () => {
  const uploadedUrls = []
  const { default: request } = await import('@/utils/request')
  const token = localStorage.getItem('token') || ''

  for (const img of window.tempImageCache) {
    try {
      const formData = new FormData()
      formData.append('file', img.file, img.file.name)

      const response = await request({
        url: '/article/upload/image',
        method: 'post',
        data: formData,
        headers: {
          'Authorization': token ? `Bearer ${token}` : ''
        }
      })

      if (response.code === 200 && response.data) {
        uploadedUrls.push({
          tempUrl: img.tempUrl,
          realUrl: response.data.url
        })
      }
    } catch (error) {
      // 图片上传失败，但文章仍可发布
    }
  }

  // 清空缓存
  window.tempImageCache = []
  return uploadedUrls
}

// ⭐ 替换编辑器中的临时 URL 为正式 URL
const replaceImageUrlsInEditor = (editor, uploadedUrls) => {
  uploadedUrls.forEach(({ tempUrl, realUrl }) => {
    // 获取编辑器内容
    const html = editor.getHtml()
    // 替换 URL
    const newHtml = html.replace(tempUrl, realUrl)
    // 设置新内容
    editor.setHtml(newHtml)
  })
}

const handleReset = () => {
  articleForm.content = ''
  articleForm.tags = []
  formRef.value.resetFields()
}

// 取消编辑
const handleCancel = () => {
  // 清理缓存的图片
  clearTempImageCache()
  const fromPage = route.query.from || '/statistics'
  router.push(fromPage)
}

// 返回
const handleBack = () => {
  // 清理缓存的图片
  clearTempImageCache()
  const fromPage = route.query.from || '/statistics'
  router.push(fromPage)
}

// ⭐ 清理临时图片缓存
const clearTempImageCache = () => {
  if (window.tempImageCache && window.tempImageCache.length > 0) {
    // 释放临时 URL
    window.tempImageCache.forEach(img => {
      URL.revokeObjectURL(img.tempUrl)
    })
    window.tempImageCache = []
  }
}

// 加载文章数据用于编辑
const loadArticleForEdit = async (id) => {
  try {
    const res = await getArticleById(id)
    if (res.code === 200 && res.data) {
      const article = res.data
      articleForm.title = article.title
      articleForm.category = article.categoryName || article.categoryId
      articleForm.tags = article.tags || []
      articleForm.content = article.content
    }
  } catch (error) {
    ElMessage.error('加载文章失败')
  }
}

const showInput = () => {
  inputVisible.value = true
  inputValue.value = ''
  // 自动聚焦
  setTimeout(() => {
    if (inputRef.value) {
      inputRef.value.focus()
    }
  }, 0)
}

const handleInputConfirm = () => {
  const value = inputValue.value.trim()
  if (value && articleForm.tags.length < 3) {
    // 检查是否重复
    if (!articleForm.tags.includes(value)) {
      articleForm.tags.push(value)
    } else {
      ElMessage.warning('标签已存在')
    }
  } else if (articleForm.tags.length >= 3) {
    ElMessage.warning('最多只能添加 3 个标签')
  }
  inputVisible.value = false
  inputValue.value = ''
}

const removeTag = (index) => {
  articleForm.tags.splice(index, 1)
}

const getTagType = (index) => {
  const types = ['primary', 'success', 'warning', 'danger']
  return types[index % types.length]
}

// 页面加载时检查是否是编辑模式
onMounted(() => {
  const id = route.query.id
  if (id) {
    isEditMode.value = true
    articleId.value = id
    loadArticleForEdit(id)
  }
})
</script>

<style scoped>
/* 容器：不再限制固定高度，允许内容自然扩展 */
.publish-content {
  padding: 15px;
  min-height: calc(100vh - 110px);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.publish-card {
  width: 100%;
  display: flex;
  flex-direction: column;
}

/* 核心：让 Element Plus 卡片主体变为 Flex 容器 */
:deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 15px 20px;
}

.publish-form {
  display: flex;
  flex-direction: column;
}

/* 让"文章内容"表单项有最小高度 */
.content-form-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 0 !important;
}

/* 深度选择器：穿透 el-form-item 内部的布局 */
:deep(.content-form-item .el-form-item__content) {
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

.editor-container {
  display: flex;
  flex-direction: column;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  min-height: 500px; /* 设置最小程序高度 */
}

:deep(.w-e-text-container) {
  flex: 1 !important;
  min-height: 400px !important; /* 设置编辑器内容区域最小高度 */
}

/* 底部按钮区域，缩小间距 */
.form-footer {
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
  flex-shrink: 0;
}

.tag-input-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  min-height: 32px;
}

.tag-limit-tip {
  font-size: 12px;
  color: #f56c6c;
  margin-left: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
}

.header-left {
  position: absolute;
  left: 0;
}

.header-center {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
</style>
