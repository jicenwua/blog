/**
 * 图片压缩工具类
 */

// 图片压缩配置
export interface CompressConfig {
  maxWidth?: number      // 最大宽度
  maxHeight?: number     // 最大高度
  quality?: number       // 压缩质量 (0.1 - 1.0)
  maxSize?: number       // 最大文件大小 (字节)
  outputFormat?: string  // 输出格式 'image/jpeg', 'image/webp'
}

// 默认配置
const DEFAULT_CONFIG: CompressConfig = {
  maxWidth: 1920,           // 最大宽度 1920px
  maxHeight: 1080,          // 最大高度 1080px
  quality: 0.8,             // 压缩质量 80%
  maxSize: 500 * 1024,      // 最大 500KB
  outputFormat: 'image/jpeg' // 输出 JPEG 格式
}

/**
 * 压缩图片
 * @param file 原始文件
 * @param config 压缩配置
 * @returns 压缩后的 Blob
 */
export const compressImage = (
  file: File, 
  config: CompressConfig = DEFAULT_CONFIG
): Promise<Blob> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    
    reader.onload = (e) => {
      const img = new Image()
      img.src = e.target?.result as string
      
      img.onload = () => {
        try {
          // 计算缩放比例
          let { width, height } = calculateSize(
            img.width, 
            img.height, 
            config.maxWidth || DEFAULT_CONFIG.maxWidth!,
            config.maxHeight || DEFAULT_CONFIG.maxHeight!
          )
          
          // 创建 canvas
          const canvas = document.createElement('canvas')
          canvas.width = width
          canvas.height = height
          
          const ctx = canvas.getContext('2d')
          if (!ctx) {
            reject(new Error('无法获取 canvas 上下文'))
            return
          }
          
          // 绘制图片（白色背景，防止透明 PNG 变黑）
          ctx.fillStyle = '#FFFFFF'
          ctx.fillRect(0, 0, width, height)
          ctx.drawImage(img, 0, 0, width, height)
          
          // 转换为 Blob
          canvas.toBlob(
            (blob) => {
              if (!blob) {
                reject(new Error('图片压缩失败'))
                return
              }
              
              // 如果设置了最大大小且超过限制，递归降低质量
              if (config.maxSize && blob.size > config.maxSize) {
                const newQuality = (config.quality || 0.8) * 0.8
                if (newQuality > 0.3) {
                  compressImage(file, { ...config, quality: newQuality })
                    .then(resolve)
                    .catch(reject)
                } else {
                  resolve(blob)
                }
              } else {
                resolve(blob)
              }
            },
            config.outputFormat || DEFAULT_CONFIG.outputFormat,
            config.quality || DEFAULT_CONFIG.quality
          )
        } catch (error) {
          reject(error)
        }
      }
      
      img.onerror = () => {
        reject(new Error('图片加载失败'))
      }
    }
    
    reader.onerror = () => {
      reject(new Error('文件读取失败'))
    }
    
    reader.readAsDataURL(file)
  })
}

/**
 * 计算缩放后的尺寸
 */
const calculateSize = (
  originalWidth: number,
  originalHeight: number,
  maxWidth: number,
  maxHeight: number
): { width: number; height: number } => {
  let width = originalWidth
  let height = originalHeight
  
  // 如果图片尺寸小于最大尺寸，不放大
  if (width <= maxWidth && height <= maxHeight) {
    return { width, height }
  }
  
  // 计算缩放比例
  const ratio = Math.min(maxWidth / width, maxHeight / height)
  width = Math.floor(width * ratio)
  height = Math.floor(height * ratio)
  
  return { width, height }
}

/**
 * 将 Blob 转换为 File
 */
export const blobToFile = (blob: Blob, fileName: string): File => {
  const file = new File([blob], fileName, {
    type: blob.type,
    lastModified: Date.now()
  })
  return file
}

/**
 * 格式化文件大小
 */
export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i]
}
