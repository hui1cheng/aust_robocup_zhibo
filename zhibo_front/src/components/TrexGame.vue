<template>
  <div class="dino-wrapper">
    <div ref="gameContainer" class="trex-game"></div>

    <div id="offline-resources" style="display: none;">
      <audio id="offline-sound-press" src="data:audio/mpeg;base64,..."></audio>
      <audio id="offline-sound-hit" src="data:audio/mpeg;base64,..."></audio>
      <audio id="offline-sound-reached" src="data:audio/mpeg;base64,..."></audio>
      </div>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref } from 'vue';
import { Runner } from '@/utils/offline.js'; // 确保路径正确
import '@/assets/css/trex.css';             // 引入必要的样式

const gameContainer = ref(null);
let runnerInstance = null;

onMounted(() => {
  if (gameContainer.value) {
    // 1. 初始化游戏实例
    // 源码中的 Runner 会自动查找 #offline-resources 里的音频
    runnerInstance = new Runner(gameContainer.value);
    
    // 2. 可选：如果你想调整初始速度或配置
    // runnerInstance.setSpeed(10);
    
    console.log('小恐龙已就绪:', runnerInstance);
  }
});

onBeforeUnmount(() => {
  // 组件销毁时停止游戏逻辑，释放内存
  if (runnerInstance) {
    runnerInstance.stop();
    runnerInstance = null;
  }
});
</script>

<style scoped>
.dino-wrapper {
  width: 100%;
  max-width: 600px; /* 根据需要调整宽度 */
  margin: 0 auto;
  position: relative;
  overflow: hidden;
}

/* 确保游戏容器有基本高度，避免初始加载时塌陷 */
.trex-game {
  width: 100%;
  height: 150px; 
}
</style>