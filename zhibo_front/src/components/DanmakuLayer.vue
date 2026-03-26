<template>
  <div class="danmaku-container" ref="container">
    <div
      v-for="d in danmakus"
      :key="d.id"
      class="danmaku-item"
      :style="{
        top: d.top + 'px',
        '--duration': d.duration + 's',
        '--color': d.color
      }"
      @animationend="removeDanmaku(d.id)"
    >
      <span class="sender">[{{ d.from }}]:</span>
      <span class="text">{{ d.text }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue';

const danmakus = ref([]);
let idCounter = 0;

// 暴露给父组件的发送方法
const add = ({ text, from = 'SYSTEM', color = 'var(--theme-color)' }) => {
  const id = idCounter++;
  const trackHeight = 30; // 每行高度
  const maxTracks = 8;    // 最大轨道数
  const track = Math.floor(Math.random() * maxTracks);
  
  danmakus.value.push({
    id,
    text,
    from,
    color,
    top: track * trackHeight + 20,
    duration: 6 + Math.random() * 4 // 随机速度 6-10s
  });
};

const removeDanmaku = (id) => {
  danmakus.value = danmakus.value.filter(d => d.id !== id);
};

defineExpose({ add });
</script>

<style scoped>
.danmaku-container {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
  z-index: 10;
}

.danmaku-item {
  position: absolute;
  left: 100%;
  white-space: nowrap;
  font-family: 'Share Tech Mono', monospace;
  font-size: 16px;
  display: flex;
  gap: 8px;
  will-change: transform;
  animation: move var(--duration) linear forwards;
  text-shadow: 0 0 5px rgba(0,0,0,0.8), 0 0 10px var(--color);
}

.sender { opacity: 0.7; font-size: 12px; align-self: center; }
.text { color: #fff; border-left: 2px solid var(--color); padding-left: 8px; }

@keyframes move {
  from { transform: translateX(0); }
  to { transform: translateX(calc(-100vw - 100%)); }
}
</style>