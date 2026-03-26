<template>
  <div 
    class="pointer-container" 
    :style="{
      transform: `translate3d(${pos.x}px, ${pos.y}px, 0) translate(-50%, -50%)`,
      width: size.w + 'px',
      height: size.h + 'px',
      opacity: isVisible ? 1 : 0
    }"
  >
    <div class="row">
      <div class="pointer-corner top-left"></div>
      <div class="pointer-corner top-right"></div>
    </div>
    <div class="row">
      <div class="pointer-corner bottom-left"></div>
      <div class="pointer-corner bottom-right"></div>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import gsap from 'gsap'; // 强烈建议引入 gsap 处理平滑跟随

const pos = reactive({ x: 0, y: 0 });
const size = reactive({ w: 30, h: 30 });
const isVisible = ref(false);
const currentTarget = ref(null);

// 核心逻辑：更新位置
const update = (e) => {
  isVisible.value = true;
  
  let targetX = e.clientX;
  let targetY = e.clientY;

  if (currentTarget.value) {
    const rect = currentTarget.value.getBoundingClientRect();
    // 目标中心点
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;
    
    // 磁吸算法：吸附在中心，但保留一点点鼠标跟随位移
    targetX = centerX + (e.clientX - centerX) * 0.2;
    targetY = centerY + (e.clientY - centerY) * 0.2;
  }

  // 使用 GSAP 平滑移动坐标，解决“框得不对”的视觉顿挫感
  gsap.to(pos, {
    x: targetX,
    y: targetY,
    duration: 0.3,
    ease: "power2.out"
  });
};

const onMouseOver = (e) => {
  const el = e.target.closest('.magnetic-target');
  if (el) {
    currentTarget.value = el;
    const rect = el.getBoundingClientRect();
    // 动态调整尺寸，比按钮大一圈（15px 间隙）
    gsap.to(size, {
      w: rect.width + 15,
      h: rect.height + 15,
      duration: 0.3
    });
  } else {
    currentTarget.value = null;
    gsap.to(size, { w: 30, h: 30, duration: 0.3 });
  }
};

onMounted(() => {
  window.addEventListener('mousemove', update);
  // 使用 mouseover 代理监听，性能更好
  window.addEventListener('mouseover', onMouseOver);
});

onUnmounted(() => {
  window.removeEventListener('mousemove', update);
  window.removeEventListener('mouseover', onMouseOver);
});
</script>

<style scoped>
.pointer-container {
  position: fixed;
  top: 0;
  left: 0;
  /* 核心：确保是以中心点对齐鼠标 */
  transform-origin: center center;
  pointer-events: none; 
  z-index: 999999;
  
  /* 强制内部元素撑开 */
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

/* 创建两行来分散四个角 */
.row {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.pointer-corner {
  width: 12px;
  height: 12px;
  border: 2px solid #00f3ff;
  box-shadow: 0 0 10px rgba(0, 243, 255, 0.8);
  /* 确保边框不会因为内部挤压变形 */
  flex-shrink: 0;
}

/* 重新精准定义四个角，避免出现多余的边 */
.top-left     { border-right: none; border-bottom: none; }
.top-right    { border-left: none; border-bottom: none; }
.bottom-left  { border-right: none; border-top: none; }
.bottom-right { border-left: none; border-top: none; }
</style>