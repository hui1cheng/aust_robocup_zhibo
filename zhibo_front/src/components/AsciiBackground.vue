<template>
  <div class="ascii-container">
    <pre class="ascii-canvas" ref="asciiContainer"></pre>
    <div class="vignette-overlay"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { parseGIF, decompressFrames } from 'https://cdn.jsdelivr.net/npm/gifuct-js@2.1.2/+esm';

const asciiContainer = ref(null);
const scale_nums = 6; // 采样密度。4更密，8更稀疏。
const texts = [];
let current_text_index = 0;
let playInterval = null;

onMounted(async () => {
  try {
    // 确保你的 public 文件夹下有 ascii.gif
    const resp = await fetch("/ascii.gif"); 
    if (!resp.ok) throw new Error("GIF loading failed");
    
    const buff = await resp.arrayBuffer();
    const gif = parseGIF(buff);
    const width = gif.lsd.width;
    const height = gif.lsd.height;

    // 解析帧
    const frames = decompressFrames(gif, true);
    
    frames.forEach(frame => {
      const canvas = document.createElement("canvas");
      const ctx = canvas.getContext("2d");
      canvas.width = frame.dims.width;
      canvas.height = frame.dims.height;

      const imageData = new ImageData(
        frame.patch,
        frame.dims.width,
        frame.dims.height
      );
      
      // 在 Canvas 绘制时稍微增加一点对比度
      ctx.putImageData(imageData, 0, 0);
      
      // 生成字符矩阵
      const frameData = ctx.getImageData(0, 0, width, height).data;
      texts.push(generateAsciiFrame(frameData, width, height));
    });

    // 播放动画 (大约 15 FPS)
    playInterval = setInterval(() => {
      if (texts.length > 0 && asciiContainer.value) {
        current_text_index = (current_text_index + 1) % texts.length;
        asciiContainer.value.innerText = texts[current_text_index];
      }
    }, 66);

  } catch (err) {
    console.error("ASCII System Error:", err);
  }
});

onUnmounted(() => {
  if (playInterval) clearInterval(playInterval);
});

// 核心逻辑：将像素映射为字符
function generateAsciiFrame(data, width, height) {
  let text = "";
  // 字符集：从最密到最稀。增加字符密集度会让颜色看起来更深。
  const charMap = ["@", "#", "8", "&", "o", ":", "*", ".", " "];
  
  for (let y = 0; y < height; y += scale_nums) {
    let row = "";
    for (let x = 0; x < width; x += scale_nums) {
      const i = (y * width + x) * 4;
      
      // 使用加权灰度算法 (人眼对绿色更敏感)
      const brightness = (data[i] * 0.299 + data[i + 1] * 0.587 + data[i + 2] * 0.114);
      
      // 映射字符 index
      const charIdx = Math.floor((brightness / 255) * (charMap.length - 1));
      row += charMap[charIdx];
    }
    text += row + "\n";
  }
  return text;
}
</script>

<style scoped>
.ascii-container {
  position: fixed;
  inset: 0;
  z-index: -2; /* 确保在所有 UI 下方 */
  background-color: #000; /* 纯黑底色让青色更亮 */
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  pointer-events: none;
}

.ascii-canvas {
  font-family: 'Courier New', Courier, monospace;
  /* 动态调整字号，确保铺满 */
  font-size: 1.1vmin; 
  line-height: 1.0;
  letter-spacing: 0.6vmin;
  
  /* 颜色深度优化 */
  color: #00f3ff; 
  opacity: 0.35; /* 颜色深度控制点：0.1 太浅，0.6 太抢眼 */
  
  /* 发光效果 */
  text-shadow: 0 0 8px rgba(0, 243, 255, 0.6);
  
  white-space: pre;
  filter: contrast(1.5) brightness(1.2); /* 强行拉高对比度 */
  user-select: none;
}

.vignette-overlay {
  position: absolute;
  inset: 0;
  /* 径向遮罩：确保中间区域（登录框位置）最通透 */
  background: radial-gradient(
    circle at center, 
    transparent 10%, 
    rgba(0, 0, 0, 0.4) 50%, 
    rgba(0, 0, 0, 0.9) 100%
  );
  pointer-events: none;
}
</style>