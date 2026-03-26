<template>
  <div class="cyber-monitor" :style="themeVars">
    <div class="grid-overlay"></div>
    <div class="scanline"></div>

    <Transition name="fade-slide">
      <div v-if="!hasSelectedRoom" class="room-picker-wrapper">
        </div>
    </Transition>

    <Transition name="fade">
      <div v-if="hasSelectedRoom" class="monitor-container">
        <header class="cyber-header">
          <div class="header-left">
            <button @click="exitRoom" class="back-btn">&lt;&lt; EXIT_SECTOR</button>
            <h1 class="glitch-text" :data-text="`ROBOCUP_${currentRoom.toUpperCase()}`">
              ROBOCUP_{{ currentRoom.toUpperCase() }}
            </h1>
          </div>
          <div class="room-info">
            <span class="count-tag">ONLINE_NODES: {{ Object.keys(matchNodes).length }}</span>
          </div>
        </header>

        <main class="monitor-main">
          <Transition name="scale-fade">
            <div v-if="activeStream" class="video-overlay-layer">
              <div class="video-window">
                <div class="window-header">
                  <span class="window-title">LIVE_FEED: {{ matchNodes[activeStream]?.displayName }}</span>
                  <button @click="closeVideo" class="close-window-btn">× CLOSE_FEED</button>
                </div>
                
                <div class="video-frame">
                  <div class="corner top-left"></div><div class="corner top-right"></div>
                  <div class="corner bottom-left"></div><div class="corner bottom-right"></div>
                  <video id="rtc_media_player" autoplay controls muted></video>
                </div>
              </div>
            </div>
          </Transition>

          <div class="nodes-grid" v-if="Object.keys(matchNodes).length > 0">
            <div 
              v-for="(info, name) in matchNodes" 
              :key="name" 
              @click="openVideo(name)"
              class="node-card"
              :class="{ 'active': activeStream === name }"
            >
              <div class="node-icon">📡</div>
              <div class="node-name">{{ info.displayName }}</div>
              <div class="node-meta">STREAM_ID: {{ name }}</div>
              <div class="node-btn">ESTABLISH_LINK</div>
            </div>
          </div>

          <div v-else class="empty-state">
            <div class="scanner-circle"></div>
            <p>NO_ACTIVE_GROUPS_DETECTED_IN_THIS_SECTOR</p>
            <div class="loading-bar"></div>
          </div>
        </main>
      </div>
    </Transition>
  </div>
</template>
<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { RoboCupStreamer } from '@/utils/srs.sdk.js';

// 基础状态
const hasSelectedRoom = ref(false); // 是否已选择房间
const currentRoom = ref('');        // 当前房间 (2d/3d/rescue)
const allStreams = ref({});         // SRS返回的所有流原始数据
const activeStream = ref('');       // 当前正在观看的流ID (为空则不显示视频)

let player = null;
let timer = null;

const rooms = [
  { key: '2d', name: '2D SOCCER', icon: '⚽', tag: 'STRATEGY_ANALYSIS' },
  { key: '3d', name: '3D SOCCER', icon: '🎮', tag: 'PHYSICS_SIMULATION' },
  { key: 'rescue', name: 'RESCUE', icon: '🚨', tag: 'TERRAIN_RECON' }
];

const roomConfig = {
  '2d': { color: '#00f3ff', icon: '⚽' },
  '3d': { color: '#ff00ff', icon: '🎮' },
  'rescue': { color: '#ff5500', icon: '🚨' }
};

// --- 计算属性：解析并过滤当前房间的小组 ---
const matchNodes = computed(() => {
  if (!currentRoom.value) return {};
  const result = {};
  
  Object.keys(allStreams.value).forEach(name => {
    // 匹配规则: livestream_房间名_小组A(_小组B)
    if (name.startsWith(`livestream_${currentRoom.value}`)) {
      const parts = name.split('_');
      const teamA = parts[2] || 'UNKNOWN_GROUP';
      const teamB = parts[3] || null;
      
      result[name] = {
        teamA: teamA.toUpperCase(),
        teamB: teamB ? teamB.toUpperCase() : null,
        displayName: teamB ? `${teamA} VS ${teamB}` : `${teamA}_UNIT`
      };
    }
  });
  return result;
});

const themeVars = computed(() => ({
  '--theme-color': roomConfig[currentRoom.value]?.color || '#00f3ff'
}));

// --- 交互函数 ---

// 选择房间进入
const enterRoom = (roomKey) => {
  currentRoom.value = roomKey;
  hasSelectedRoom.value = true;
  fetchStatus();
};

// 退出房间回到大厅
const exitRoom = () => {
  closeVideo();
  hasSelectedRoom.value = false;
  currentRoom.value = '';
};

// 打开视频窗口
const openVideo = async (streamName) => {
  activeStream.value = streamName;
  // 等待 DOM 更新后初始化播放器
  await nextTick();
  initPlayer();
};

// 关闭视频窗口
const closeVideo = () => {
  if (player) {
    player.close();
    player = null;
  }
  activeStream.value = '';
};

// 定时获取SRS流状态
const fetchStatus = async () => {
  try {
    const res = await fetch('/api/v1/srs/streams');
    const data = await res.json();
    allStreams.value = data;

    // 如果当前正在看的流断开了，自动关闭窗口
    if (activeStream.value && !allStreams.value[activeStream.value]) {
      closeVideo();
    }
  } catch (err) {
    console.warn("SRS_OFFLINE", err);
  }
};

// 初始化 WebRTC 播放器
const initPlayer = async () => {
  if (!activeStream.value) return;
  player = RoboCupStreamer();
  const srsHost = window.location.hostname;
  
  try {
    await player.play(`webrtc://${srsHost}/live/${activeStream.value}`, `/rtc`);
    const videoDom = document.getElementById('rtc_media_player');
    if (videoDom) {
      videoDom.srcObject = player.stream;
    }
  } catch (e) {
    console.error("RTC_PLAY_ERROR", e);
    activeStream.value = '';
  }
};

onMounted(() => {
  timer = setInterval(fetchStatus, 3000);
});

onUnmounted(() => {
  clearInterval(timer);
  if (player) player.close();
});
</script>
<style scoped>
/* 基础布局 */
.cyber-monitor {
  position: fixed;
  inset: 0;
  background: #020406;
  color: var(--theme-color);
  font-family: 'Courier New', monospace;
  overflow: hidden;
}

/* 装饰背景 */
.grid-overlay {
  position: absolute; inset: 0;
  background-image: linear-gradient(var(--theme-color) 1px, transparent 1px),
                    linear-gradient(90deg, var(--theme-color) 1px, transparent 1px);
  background-size: 40px 40px; opacity: 0.05; pointer-events: none;
}
.scanline {
  position: absolute; top: 0; width: 100%; height: 2px;
  background: var(--theme-color); box-shadow: 0 0 10px var(--theme-color);
  animation: scan 8s linear infinite; opacity: 0.3;
}
@keyframes scan { from { top: -10% } to { top: 110% } }

/* 房间选择卡片 */
.room-picker-wrapper {
  position: relative; z-index: 10; height: 100%;
  display: flex; flex-direction: column; justify-content: center; align-items: center;
  background: rgba(0,0,0,0.85);
}
.picker-grid { display: flex; gap: 30px; }
.room-card {
  width: 240px; padding: 40px; background: rgba(10, 20, 30, 0.8);
  border: 1px solid rgba(255,255,255,0.1); cursor: pointer;
  transition: 0.3s cubic-bezier(0.4, 0, 0.2, 1); text-align: center;
}
.room-card:hover { transform: translateY(-10px); border-color: var(--theme-color); box-shadow: 0 0 20px var(--theme-color); }
.room-icon { font-size: 50px; margin-bottom: 20px; }

/* 内部视图容器 */
.monitor-container { height: 100%; display: flex; flex-direction: column; padding: 20px; box-sizing: border-box; }
.cyber-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--theme-color); padding-bottom: 15px; }

/* 队伍卡片列表网格 */
.nodes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 25px;
  padding: 40px 0;
  overflow-y: auto;
}
.group-card {
  background: rgba(10, 20, 30, 0.7);
  border: 1px solid rgba(0, 243, 255, 0.15);
  padding: 30px;
  cursor: pointer;
  transition: 0.3s;
  position: relative;
  overflow: hidden;
}
.group-card:hover {
  border-color: var(--theme-color);
  background: rgba(0, 243, 255, 0.05);
  transform: scale(1.02);
}
.group-title { font-size: 20px; font-weight: bold; margin-bottom: 15px; color: #fff; }
.group-id { font-size: 10px; opacity: 0.5; font-family: monospace; }
.connect-btn {
  margin-top: 20px; display: inline-block;
  font-size: 12px; padding: 5px 15px;
  border: 1px solid var(--theme-color);
}

/* 视频弹出窗口层 */
.video-modal-overlay {
  position: fixed; inset: 0; z-index: 100;
  background: rgba(0, 0, 0, 0.9);
  display: flex; justify-content: center; align-items: center;
  backdrop-filter: blur(8px);
}
.video-window {
  width: 90%; max-width: 1100px;
  background: #000; border: 1px solid var(--theme-color);
  box-shadow: 0 0 50px rgba(0,0,0,1);
}
.window-bar {
  display: flex; justify-content: space-between; align-items: center;
  padding: 10px 20px; background: var(--theme-color); color: #000;
}
.window-title { font-weight: bold; letter-spacing: 1px; }
.close-btn { 
  background: #000; color: var(--theme-color); border: none; 
  padding: 4px 12px; cursor: pointer; font-weight: bold;
}

.video-frame { position: relative; padding: 10px; background: #000; }
#rtc_media_player { width: 100%; height: auto; display: block; }

/* 空状态 */
.empty-state {
  flex: 1; display: flex; flex-direction: column;
  justify-content: center; align-items: center;
  opacity: 0.4;
}
.scanner-circle {
  width: 60px; height: 60px; border: 2px solid var(--theme-color);
  border-radius: 50%; border-top-color: transparent;
  animation: spin 1s linear infinite; margin-bottom: 20px;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* 动画 */
.fade-enter-active, .fade-leave-active { transition: 0.4s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.scale-enter-active, .scale-leave-active { transition: 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); }
.scale-enter-from, .scale-leave-to { transform: scale(0.8); opacity: 0; }
</style>