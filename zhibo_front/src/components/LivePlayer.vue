<template>
  <div class="cyber-monitor">
    <div class="grid-overlay"></div>
    <div class="scanline"></div>

    <header class="cyber-header">
      <div class="header-content">
        <h1 class="glitch-text" data-text="ROBOCUP_SYSTEM_v2.0">ROBOCUP_SYSTEM_v2.0</h1>
        <div :class="['status-panel', isLive ? 'live' : 'offline']">
          <span class="pulse-dot"></span>
          <span class="status-text">{{ isLive ? 'LINK_ESTABLISHED' : 'SIGNAL_LOST' }}</span>
        </div>
      </div>
      <div class="top-bar"></div>
    </header>

    <main class="monitor-main">
      <div v-if="isLive" class="video-container">
        <div class="video-frame">
          <div class="corner top-left"></div>
          <div class="corner top-right"></div>
          <div class="corner bottom-left"></div>
          <div class="corner bottom-right"></div>
          
          <video id="rtc_media_player" autoplay controls muted class="glitch-video"></video>
          
          <div class="video-info-overlay">
            <span class="stream-id">ID: {{ currentStream }}</span>
            <span class="bitrate">BR: 2500kbps</span>
            <span class="latency">LATENCY: LOW</span>
          </div>
        </div>
      </div>

      <div v-else class="offline-terminal">
        <div class="error-code">ERROR_404: NO_SIGNAL_DETECTED</div>
        <div class="dino-placeholder">🦖</div>
        <p class="loading-text">RESCANNING SATELLITE FREQUENCY...</p>
      </div>
    </main>

    <footer v-if="Object.keys(allStreams).length > 0" class="control-footer">
      <div class="footer-label">ACTIVE_NODES:</div>
      <div class="button-group">
        <button 
          v-for="(ip, name) in allStreams" 
          :key="name" 
          @click="switchStream(name)"
          :class="['cyber-btn', { active: currentStream === name }]"
        >
          <span class="btn-prefix">>></span> {{ name }}
        </button>
      </div>
    </footer>
  </div>
</template>
<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { RoboCupStreamer } from '@/utils/srs.sdk.js';

const isLive = ref(false);
const currentStream = ref('livestream');
const allStreams = ref({});
let player = null;
let timer = null;

const checkStatus = async () => {
  try {
    const res = await fetch('/api/v1/srs/streams');
    const data = await res.json();
    allStreams.value = data;
    
    const streamExists = !!data[currentStream.value];
    
    // 只有在没有播放且发现有流时才启动
    if (streamExists && !isLive.value) {
      isLive.value = true;
      setTimeout(initPlayer, 500);
    } 
    // 如果流没了，记录日志，但不立刻 close 播放器，
    // 让 WebRTC 自己的超时机制去处理，防止轮询抖动
    else if (!streamExists && isLive.value) {
      console.warn("Signal may be lost, but keeping connection alive...");
    }
  } catch (err) { console.error("API Error", err); }
};

const initPlayer = async () => {
  if (player) player.close();
  player = RoboCupStreamer();
  const url = `webrtc://${window.location.hostname}/live/${currentStream.value}`;
  console.log(window.location.hostname)
  console.log(url)
  const apiUrl = `http://${window.location.hostname}:1985/rtc/v1/play/`

  try {
    await player.play(url,apiUrl);
    const videoDom = document.getElementById('rtc_media_player');
    if (videoDom) videoDom.srcObject = player.stream;
  } catch (e) { console.error("播放失败:", e); }
};

const switchStream = (name) => {
  currentStream.value = name;
  isLive.value = false;
  checkStatus();
};

onMounted(() => {
  checkStatus();
  timer = setInterval(checkStatus, 3000);
});

onUnmounted(() => {
  if (timer) clearInterval(timer);
  if (player) player.close();
});
</script>

<style scoped>
/* 核心容器：全屏沉浸感 */
.cyber-monitor {
  position: relative;
  min-height: 100vh;
  background: #020406;
  color: #00f3ff;
  font-family: 'Courier New', Courier, monospace;
  overflow: hidden;
  padding: 20px;
}

/* 背景装饰 */
.grid-overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: linear-gradient(rgba(0, 243, 255, 0.05) 1px, transparent 1px),
                    linear-gradient(90deg, rgba(0, 243, 255, 0.05) 1px, transparent 1px);
  background-size: 30px 30px;
  pointer-events: none;
}

.scanline {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 2px;
  background: rgba(0, 243, 255, 0.1);
  box-shadow: 0 0 10px #00f3ff;
  animation: scan 8s linear infinite;
  z-index: 10;
  pointer-events: none;
}

@keyframes scan {
  from { top: -10%; }
  to { top: 110%; }
}

/* 头部样式 */
.cyber-header {
  border-bottom: 2px solid #00f3ff;
  padding-bottom: 10px;
  margin-bottom: 30px;
  position: relative;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-panel {
  display: flex;
  align-items: center;
  padding: 5px 15px;
  border: 1px solid currentColor;
}

.status-panel.live { color: #ff00ff; border-color: #ff00ff; box-shadow: 0 0 10px rgba(255, 0, 255, 0.3); }
.status-panel.offline { color: #555; border-color: #333; }

.pulse-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: currentColor;
  margin-right: 10px;
  animation: pulse 1s infinite alternate;
}

@keyframes pulse { from { opacity: 0.3; } to { opacity: 1; } }

/* 视频框设计 */
.video-container {
  max-width: 1000px;
  margin: 0 auto;
}

.video-frame {
  position: relative;
  background: rgba(0, 0, 0, 0.8);
  padding: 10px;
  border: 1px solid rgba(0, 243, 255, 0.3);
}

.corner {
  position: absolute;
  width: 20px; height: 20px;
  border: 3px solid #00f3ff;
}
.top-left { top: -5px; left: -5px; border-right: 0; border-bottom: 0; }
.top-right { top: -5px; right: -5px; border-left: 0; border-bottom: 0; }
.bottom-left { bottom: -5px; left: -5px; border-right: 0; border-top: 0; }
.bottom-right { bottom: -5px; right: -5px; border-left: 0; border-top: 0; }

#rtc_media_player {
  width: 100%;
  display: block;
  filter: sepia(20%) hue-rotate(160deg);
}

.video-info-overlay {
  position: absolute;
  bottom: 20px; left: 20px;
  background: rgba(0, 0, 0, 0.7);
  padding: 5px 10px;
  font-size: 12px;
  color: #00f3ff;
  border-left: 3px solid #ff00ff;
}

/* 离线状态 */
.offline-terminal {
  padding: 100px 0;
  text-align: center;
}

.error-code { color: #ff00ff; font-weight: bold; margin-bottom: 20px; }

.dino-placeholder {
  font-size: 80px;
  filter: grayscale(1) contrast(2);
  opacity: 0.5;
  margin: 20px 0;
}

/* 底部按钮 */
.control-footer {
  margin-top: 50px;
  border-top: 1px solid #333;
  padding-top: 20px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 15px;
  flex-wrap: wrap;
}

.cyber-btn {
  background: transparent;
  border: 1px solid #00f3ff;
  color: #00f3ff;
  padding: 10px 20px;
  cursor: pointer;
  transition: 0.3s;
}

.cyber-btn:hover, .cyber-btn.active {
  background: rgba(0, 243, 255, 0.2);
  box-shadow: 0 0 15px rgba(0, 243, 255, 0.5);
}

.btn-prefix { color: #ff00ff; margin-right: 5px; }
</style>