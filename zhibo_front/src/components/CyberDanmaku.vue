<template>
  <div class="danmaku-container">
    <div 
      v-for="dm in queue" 
      :key="dm.id"
      class="dm-item"
      :style="{ 
        top: dm.top + 'px', 
        '--speed': dm.speed + 's', 
        '--color': dm.color 
      }"
    >
      <span v-if="dm.type !== 'USER'" class="dm-tag">[{{ dm.type }}]</span>
      <span class="dm-text">{{ dm.text }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, defineExpose } from 'vue';

const queue = ref([]);
let dmId = 0;
const COLORS = ['#00f3ff', '#ff00ff', '#70ff00', '#ffffff', '#ffff00'];

// 暴露给父组件的方法
const push = (text, type = 'USER') => {
  const id = dmId++;
  // 轨道逻辑：将视频高度分为 15 条轨道，每轨 28px，避开最顶部 40px
  const track = Math.floor(Math.random() * 15);
  const top = 40 + (track * 28); 
  const speed = Math.random() * 6 + 12; // 7-10秒

  const newDm = {
    id,
    text,
    type,
    top,
    speed,
    // 如果是系统错误 SYS 则固定红色，否则随机颜色
    color: type === 'SYS' ? '#ff0055' : COLORS[Math.floor(Math.random() * COLORS.length)]
  };

  queue.value.push(newDm);

  // 动画结束后自动清理，释放内存
  setTimeout(() => {
    queue.value = queue.value.filter(d => d.id !== id);
  }, speed * 1000);
};

// 必须通过 defineExpose 暴露 push 方法，父组件才能调用
defineExpose({ push });
</script>

<style scoped>
.danmaku-container {
  position: absolute;
  inset: 0;
  pointer-events: none; /* 穿透层，不影响点击下方的视频控制条 */
  overflow: hidden;
  z-index: 100;
}

.dm-item {
  position: absolute;
  right: -100%;
  white-space: nowrap;
  font-family: 'Consolas', 'Monaco', monospace;
  font-weight: bold;
  font-size: 16px;
  color: var(--color);
  /* 黑色描边加外发光，确保在复杂的监控画面上清晰可见 */
  text-shadow: 2px 2px 2px #000, 0 0 10px var(--color);
  will-change: transform;
  animation: slide-left var(--speed) linear forwards;
  display: flex;
  align-items: center;
}

.dm-tag {
  font-size: 10px;
  background: rgba(0, 0, 0, 0.7);
  border: 1px solid var(--color);
  padding: 0 4px;
  margin-right: 8px;
  text-shadow: none;
}

@keyframes slide-left {
  from { transform: translateX(0); }
  to { transform: translateX(-300vw); } /* 确保彻底划出屏幕 */
}
</style>