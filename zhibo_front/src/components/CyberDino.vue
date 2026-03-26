<template>
  <div class="logo-wrapper">
    <div class="energy-orbit"></div>
    
    <div class="cyber-logo-container">
      <img src="@/assets/AUST_logo.png" alt="AUST Logo" class="logo-png" />
      
      <img src="@/assets/AUST_logo.png" class="png-glitch purple" />
      <img src="@/assets/AUST_logo.png" class="png-glitch cyan" />
    </div>
  </div>
</template>
<style scoped>
.logo-wrapper {
  position: relative;
  width: 140px; height: 120px;
  display: flex; justify-content: center; align-items: center;
  margin-bottom: 20px;
}

.cyber-logo-container {
  position: relative;
  width: 80px; height: 80px;
  /* 基础发光：对 PNG 生效的霓虹滤镜 */
  filter: drop-shadow(0 0 8px rgba(0, 243, 255, 0.8));
  animation: breathe 4s ease-in-out infinite;
}

.logo-png {
  width: 100%; height: 100%;
  object-fit: contain;
  /* 强制将 PNG 改为青色 (brightness(0)设为全黑，invert(1)设为全白，sepia设为青色) */
  filter: brightness(0) invert(1) sepia(1) saturate(10000%) hue-rotate(180deg);
}

.png-glitch {
  position: absolute; inset: 0;
  width: 100%; height: 100%;
  object-fit: contain;
  opacity: 0; z-index: 2;
  pointer-events: none;
}

.purple {
  filter: brightness(0) invert(1) sepia(1) saturate(10000%) hue-rotate(280deg);
  animation: glitch-anim 4s infinite 2s;
}

.cyan {
  filter: brightness(0) invert(1) sepia(1) saturate(10000%) hue-rotate(180deg);
  animation: glitch-anim 4s infinite;
}
.logo-wrapper {
  position: relative;
  width: 140px;
  height: 140px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
}

.cyber-logo-body {
  position: relative;
  width: 80px;
  height: 80px;
  /* 基础发光 */
  filter: drop-shadow(0 0 8px rgba(0, 243, 255, 0.6));
  animation: breathe 4s ease-in-out infinite;
}

.source-svg {
  width: 100%;
  height: 100%;
  position: relative;
  z-index: 5;
  /* 强制改为白色或青色以符合暗色主题 */
  filter: brightness(0) invert(1) drop-shadow(0 0 2px #00f3ff);
}

/* 故障重影样式 */
.glitch-layer {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  opacity: 0;
  z-index: 1;
  pointer-events: none;
}

.purple {
  filter: brightness(0) invert(1) sepia(1) saturate(10000%) hue-rotate(280deg);
  animation: glitch-anim 4s infinite 2s;
}

.cyan {
  filter: brightness(0) invert(1) sepia(1) saturate(10000%) hue-rotate(180deg);
  animation: glitch-anim 4s infinite;
}

/* 能量轨道 */
.energy-orbit {
  position: absolute;
  width: 110px;
  height: 110px;
  border: 1px solid rgba(0, 243, 255, 0.2);
  border-radius: 40% 60% 65% 35% / 40% 45% 55% 60%;
  animation: orbit-rotate 12s linear infinite;
  box-shadow: inset 0 0 10px rgba(0, 243, 255, 0.1);
}

/* --- 动画帧 --- */

@keyframes breathe {
  0%, 100% { transform: scale(1); filter: drop-shadow(0 0 8px rgba(0, 243, 255, 0.4)); }
  50% { transform: scale(1.05); filter: drop-shadow(0 0 15px rgba(0, 243, 255, 0.8)); }
}

@keyframes glitch-anim {
  0%, 90%, 100% { opacity: 0; transform: translate(0); }
  92% { opacity: 0.8; transform: translate(-3px, 2px); }
  94% { opacity: 0.8; transform: translate(3px, -2px); }
  96% { opacity: 0.8; transform: translate(-1px, 3px); }
}

@keyframes orbit-rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>