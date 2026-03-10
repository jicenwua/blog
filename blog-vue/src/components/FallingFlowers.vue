<template>
  <div class="falling-flowers" ref="flowerContainer"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const flowerContainer = ref(null)
let petalInterval = null

// 创建单个花瓣
const createPetal = () => {
  if (!flowerContainer.value) return
  
  const petal = document.createElement('div')
  petal.className = 'flower-petal'
  
  // 随机大小
  const size = Math.random() * 10 + 8 // 8-18px
  petal.style.width = `${size}px`
  petal.style.height = `${size}px`
  
  // 随机位置
  petal.style.left = `${Math.random() * 100}%`
  
  // 随机动画时间
  const duration = Math.random() * 5 + 5 // 5-10s
  petal.style.animationDuration = `${duration}s`
  
  // 随机延迟
  petal.style.animationDelay = `${Math.random() * 2}s`
  
  flowerContainer.value.appendChild(petal)
  
  // 移除花瓣
  setTimeout(() => {
    if (petal.parentNode) {
      petal.parentNode.removeChild(petal)
    }
  }, duration * 1000)
}

// 启动落花效果
const startFallingFlowers = () => {
  // 初始创建一批花瓣
  for (let i = 0; i < 15; i++) {
    setTimeout(() => {
      createPetal()
    }, i * 200)
  }
  
  // 持续创建花瓣
  petalInterval = setInterval(() => {
    const count = Math.floor(Math.random() * 3) + 1 // 每次 1-3 个
    for (let i = 0; i < count; i++) {
      createPetal()
    }
  }, 800)
}

// 清理花瓣
const clearFlowers = () => {
  if (flowerContainer.value) {
    flowerContainer.value.innerHTML = ''
  }
}

// 暴露方法给父组件
defineExpose({
  startFallingFlowers,
  clearFlowers
})

onMounted(() => {
  startFallingFlowers()
})

onUnmounted(() => {
  if (petalInterval) {
    clearInterval(petalInterval)
  }
  clearFlowers()
})
</script>

<style scoped>
.falling-flowers {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 9999;
  overflow: hidden;
}

:deep(.flower-petal) {
  position: absolute;
  background: linear-gradient(135deg, #ffb7c5 0%, #ff9eb5 100%);
  border-radius: 50% 0 50% 0;
  opacity: 0.6;
  animation: falling-petal linear forwards;
  top: -20px;
}

@keyframes falling-petal {
  0% {
    opacity: 0;
    transform: translateY(0) rotate(0deg) translateX(0px);
  }
  10% {
    opacity: 0.8;
  }
  90% {
    opacity: 0.6;
  }
  100% {
    opacity: 0;
    transform: translateY(100vh) rotate(720deg) translateX(100px);
  }
}
</style>
