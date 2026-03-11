// 默认图片配置列表
// 这些图片将用于文章列表展示，按顺序轮询使用
// 可以将此文件中的 URL 替换为实际的本地图片路径（放在 /image 目录下）

export const defaultImages = [
  '/image/108755155_p0_master1200.jpg',
  '/image/142078358_p0_master1200.jpg',
  '/image/142112096_p0_master1200.jpg',
]

/**
 * 获取随机图片
 * @returns {string} - 图片路径
 */
export function getRotatingImage() {
  if (!defaultImages || defaultImages.length === 0) {
    // 如果没有配置图片，返回一个默认占位图
    return 'https://via.placeholder.com/400x250?text=Article+Image'
  }
  
  // 使用 Math.random() 实现随机选择
  const imageIndex = Math.floor(Math.random() * defaultImages.length)
  return defaultImages[imageIndex]
}
