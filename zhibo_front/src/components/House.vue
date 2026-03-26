<template>
  <div class="cyber-monitor">
    <div class="grid-overlay"></div>
    <div class="scanline"></div>

    <!-- 头部增加房间切换 -->
    <header class="cyber-header">
      <div class="header-content">
        <h1 class="glitch-text" :data-text="`ROBOCUP_${currentRoom.toUpperCase()}_SYSTEM_v2.0`">
          ROBOCUP_{{ currentRoom.toUpperCase() }}_SYSTEM_v2.0
        </h1>
        <div :class="['status-panel', isLive ? 'live' : 'offline']">
          <span class="pulse-dot"></span>
          <span class="status-text">{{ isLive ? 'LINK_ESTABLISHED' : 'SIGNAL_LOST' }}</span>
        </div>
      </div>
      <!-- 房间切换按钮 -->
      <div class="room-switcher">
        <button 
          v-for="room in rooms" 
          :key="room.key" 
          @click="switchRoom(room.key)"
          :class="['room-btn', { active: currentRoom === room.key }]"
        >
          <span class="room-icon">{{ room.icon }}</span> {{ room.name }}
        </button>
      </div>
      <div class="top-bar"></div>
    </header>

    <main class="monitor-main">
      <div v-if="isLive" class="video-container">
        <div class="video-frame" :class="`theme-${currentRoom}`">
          <div class="corner top-left"></div>
          <div class="corner top-right"></div>
          <div class="corner bottom-left"></div>
          <div class="corner bottom-right"></div>
          
          <video id="rtc_media_player" autoplay controls muted class="glitch-video"></video>
          
          <div class="video-info-overlay">
            <span class="stream-id">ID: {{ currentStream }}</span>
            <span class="bitrate">BR: {{ roomConfig[currentRoom].bitrate }}</span>
            <span class="latency">LATENCY: {{ roomConfig[currentRoom].latency }}</span>
            <span class="room-tag">{{ roomConfig[currentRoom].tag }}</span>
          </div>
        </div>
      </div>

      <div v-else class="offline-terminal" :class="`theme-${currentRoom}`">
        <div class="error-code">ERROR_404: NO_{{ currentRoom.toUpperCase() }}_SIGNAL_DETECTED</div>
        <div class="dino-placeholder">{{ roomConfig[currentRoom].placeholderIcon }}</div>
        <p class="loading-text">RESCANNING {{ currentRoom.toUpperCase() }} FREQUENCY...</p>
      </div>
    </main>

    <!-- 流切换按钮（按房间过滤） -->
    <footer v-if="Object.keys(filteredStreams).length > 0" class="control-footer">
      <div class="footer-label">ACTIVE_{{ currentRoom.toUpperCase() }}_NODES:</div>
      <div class="button-group">
        <button 
          v-for="(ip, name) in filteredStreams" 
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
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { RoboCupStreamer } from '@/utils/srs.sdk.js';

// 核心状态
const isLive = ref(false);
const currentRoom = ref('2d'); // 默认2D房间
const currentStream = ref('livestream_2d'); // 各房间默认流
const allStreams = ref({});
let player = null;
let timer = null;

// 房间配置（差异化主题）
const rooms = ref([
  { key: '2d', name: '2D SOCCER', icon: '⚽', tag: '2D SOCCER' },
  { key: '3d', name: '3D SOCCER', icon: '🎮', tag: '3D SIMULATION' },
  { key: 'rescue', name: 'RESCUE', icon: '🚨', tag: 'DISASTER RESCUE' }
]);

// 各房间差异化配置
const roomConfig = ref({
  '2d': { 
    bitrate: '2500kbps', 
    latency: 'LOW', 
    placeholderIcon: '⚽',
    themeColor: '#00f3ff' 
  },
  '3d': { 
    bitrate: '4000kbps', 
    latency: 'MEDIUM', 
    placeholderIcon: '🎮',
    themeColor: '#ff00ff' 
  },
  rescue: { 
    bitrate: '3000kbps', 
    latency: 'HIGH', 
    placeholderIcon: '🚨',
    themeColor: '#ff5500' 
  }
});

// 按当前房间过滤流（流名称建议包含房间标识：livestream_2d/livestream_3d/livestream_rescue）
const filteredStreams = computed(() => {
  const filterKey = currentRoom.value;
  return Object.fromEntries(
    Object.entries(allStreams.value).filter(([name]) => 
      name.includes(filterKey) || (filterKey === '2d' && !name.includes('3d') && !name.includes('rescue'))
    )
  );
});

// 检查流状态（按房间适配）
const checkStatus = async () => {
  try {
    const res = await fetch('/api/v1/srs/streams');
    const data = await res.json();
    allStreams.value = data;
    
    // 按当前房间获取对应流
    const roomStreams = filteredStreams.value;
    const streamExists = !!roomStreams[currentStream.value];
    
    // 只有在没有播放且发现有流时才启动
    if (streamExists && !isLive.value) {
      isLive.value = true;
      setTimeout(initPlayer, 500);
    } 
    // 如果流没了，记录日志，但不立刻 close 播放器
    else if (!streamExists && isLive.value) {
      console.warn(`Signal may be lost for ${currentRoom.value} room, keeping connection alive...`);
    }
  } catch (err) { 
    console.error(`API Error for ${currentRoom.value} room`, err); 
  }
};

const initPlayer = async () => {
  if (player) player.close();
  player = RoboCupStreamer();
  
  const srsHost = "192.168.31.203"; 
  const url = `webrtc://${srsHost}/live/${currentStream.value}`;

  // 关键：因为你的 Vite 已经帮你拼好了 /rtc/v1/play
  // 所以这里只需要写 /rtc 即可
  const apiUrl = `/rtc`; 

  try {
    await player.play(url, apiUrl);
    const videoDom = document.getElementById('rtc_media_player');
    if (videoDom) {
      videoDom.srcObject = player.stream;
      videoDom.play().catch(e => console.warn("Auto-play blocked:", e));
    }
  } catch (e) { 
    console.error("播放失败:", e); 
    isLive.value = false;
  }
};

// 切换流
const switchStream = (name) => {
  currentStream.value = name;
  isLive.value = false;
  checkStatus();
};

// 切换房间
const switchRoom = (roomKey) => {
  currentRoom.value = roomKey;
  // 切换房间时重置默认流
  currentStream.value = `livestream_${roomKey}`;
  isLive.value = false;
  checkStatus();
};

// 生命周期
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

/* 头部样式 - 增加房间切换 */
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
  margin-bottom: 15px;
}

/* 房间切换按钮 */
.room-switcher {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  justify-content: center;
}

.room-btn {
  background: transparent;
  border: 1px solid #00f3ff;
  color: #00f3ff;
  padding: 5px 15px;
  cursor: pointer;
  transition: 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.room-btn.active {
  background: rgba(0, 243, 255, 0.2);
  box-shadow: 0 0 10px rgba(0, 243, 255, 0.5);
}

.room-btn:hover {
  border-color: #ff00ff;
  color: #ff00ff;
}

.room-icon {
  font-size: 16px;
  margin-right: 5px;
}

/* 不同房间主题样式 */
.room-btn.active[data-room="2d"] { border-color: #00f3ff; color: #00f3ff; }
.room-btn.active[data-room="3d"] { border-color: #ff00ff; color: #ff00ff; }
.room-btn.active[data-room="rescue"] { border-color: #ff5500; color: #ff5500; }

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

/* 视频框设计 - 按房间区分主题 */
.video-container {
  max-width: 1000px;
  margin: 0 auto;
}

.video-frame {
  position: relative;
  background: rgba(0, 0, 0, 0.8);
  padding: 10px;
  border: 1px solid rgba(0, 243, 255, 0.3);
  transition: all 0.3s ease;
}

/* 2D房间主题色 */
.video-frame.theme-2d {
  border-color: rgba(0, 243, 255, 0.5);
  box-shadow: 0 0 15px rgba(0, 243, 255, 0.1);
}
.video-frame.theme-2d .corner { border-color: #00f3ff; }
.video-frame.theme-2d .video-info-overlay { border-left-color: #00f3ff; }

/* 3D房间主题色 */
.video-frame.theme-3d {
  border-color: rgba(255, 0, 255, 0.5);
  box-shadow: 0 0 15px rgba(255, 0, 255, 0.1);
}
.video-frame.theme-3d .corner { border-color: #ff00ff; }
.video-frame.theme-3d .video-info-overlay { border-left-color: #ff00ff; }

/* 救援房间主题色 */
.video-frame.theme-rescue {
  border-color: rgba(255, 85, 0, 0.5);
  box-shadow: 0 0 15px rgba(255, 85, 0, 0.1);
}
.video-frame.theme-rescue .corner { border-color: #ff5500; }
.video-frame.theme-rescue .video-info-overlay { border-left-color: #ff5500; }

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
  transition: filter 0.3s ease;
}

.video-info-overlay {
  position: absolute;
  bottom: 20px; left: 20px;
  background: rgba(0, 0, 0, 0.7);
  padding: 5px 10px;
  font-size: 12px;
  color: inherit;
  border-left: 3px solid #ff00ff;
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.room-tag {
  text-transform: uppercase;
  font-weight: bold;
}

/* 离线状态 - 按房间区分 */
.offline-terminal {
  padding: 100px 0;
  text-align: center;
  transition: color 0.3s ease;
}

.offline-terminal.theme-2d { color: #00f3ff; }
.offline-terminal.theme-3d { color: #ff00ff; }
.offline-terminal.theme-rescue { color: #ff5500; }

.error-code { 
  font-weight: bold; 
  margin-bottom: 20px;
  font-size: 18px;
}

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

/* 按钮按房间主题变色 */
.control-footer.theme-2d .cyber-btn { border-color: #00f3ff; color: #00f3ff; }
.control-footer.theme-3d .cyber-btn { border-color: #ff00ff; color: #ff00ff; }
.control-footer.theme-rescue .cyber-btn { border-color: #ff5500; color: #ff5500; }

.cyber-btn:hover, .cyber-btn.active {
  background: rgba(0, 243, 255, 0.2);
  box-shadow: 0 0 15px rgba(0, 243, 255, 0.5);
}

/* 不同房间按钮hover效果 */
.theme-3d .cyber-btn:hover, .theme-3d .cyber-btn.active {
  background: rgba(255, 0, 255, 0.2);
  box-shadow: 0 0 15px rgba(255, 0, 255, 0.5);
}

.theme-rescue .cyber-btn:hover, .theme-rescue .cyber-btn.active {
  background: rgba(255, 85, 0, 0.2);
  box-shadow: 0 0 15px rgba(255, 85, 0, 0.5);
}

.btn-prefix { 
  margin-right: 5px; 
}
.theme-2d .btn-prefix { color: #00f3ff; }
.theme-3d .btn-prefix { color: #ff00ff; }
.theme-rescue .btn-prefix { color: #ff5500; }

.footer-label {
  text-align: center;
  margin-bottom: 15px;
  color: inherit;
  text-transform: uppercase;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 10px;
    align-items: center;
  }
  
  .video-info-overlay {
    flex-direction: column;
    gap: 5px;
    bottom: 10px;
    left: 10px;
    font-size: 10px;
  }
  
  .room-switcher {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>