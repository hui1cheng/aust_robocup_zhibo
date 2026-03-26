<template>
  <div class="monitor-view" :style="`--theme-color: ${config.color}`">
    <header class="header">
      <button @click="$router.push('/house')" class="back-btn">&lt;&lt; BACK</button>
      <h2 class="room-title">SECTOR_{{ props.type.toUpperCase() }}</h2>
      <div class="node-count">ONLINE: {{ nodesCount }}</div>
    </header>

    <div class="nodes-grid" v-if="nodesCount > 0">
      <div v-for="(info, id) in matchNodes" :key="id" @click="openVideo(id)" class="group-card">
        <div class="group-title">{{ info.displayName }}</div>
        <div class="group-id">{{ id }}</div>
        <div class="btn">LINK_START</div>
      </div>
    </div>

    <div v-else class="empty">
      <div class="scanner"></div>
      <p>SCANNING_FOR_SIGNALS...</p>
    </div>

    <div v-if="activeStream" class="modal">
      <div class="window">
        <div class="bar">
          <span>LIVE_FEED: {{ matchNodes[activeStream]?.displayName }}</span>
          <button @click="closeVideo" class="close-btn">TERMINATE_X</button>
        </div>
        
        <div class="video-body">
          <div class="player-container">
            <video id="rtc_media_player" autoplay controls muted></video>
            <CyberDanmaku ref="danmakuRef" />
          </div>

          <div class="console-box">
            <span class="prompt">ROOT@{{ props.type.toUpperCase() }}:~#</span>
            <input 
              v-model="inputMsg" 
              @keyup.enter="handleSend" 
              placeholder="Type message and press Enter..."
              autofocus
            />
            <button @click="handleSend" class="exec-btn">EXECUTE</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { RoboCupStreamer } from '@/utils/srs.sdk.js';
// 1. 引入你刚才封装的纯前端弹幕组件
import CyberDanmaku from '@/components/CyberDanmaku.vue'; 

// --- 基础状态 ---
const props = defineProps(['type']); 
const allStreams = ref({});
const activeStream = ref('');
const inputMsg = ref('');
const danmakuRef = ref(null);

// --- 播放器与定时器 ---
let player = null;
let statusTimer = null;
let autoSystemDmTimer = null; // 自动模拟弹幕

// --- 样式配置 ---
const config = computed(() => {
  const themes = { '2d': '#00f3ff', '3d': '#ff00ff', 'rescue': '#ff5500' };
  return { color: themes[props.type] || '#00f3ff' };
});

// --- 逻辑过滤 ---
const matchNodes = computed(() => {
  const res = {};
  const entries = Object.entries(allStreams.value);
  entries.forEach(([name, info]) => {
    // 适配不同的 SRS API 返回结构
    if (name.startsWith(`livestream_${props.type}`)) {
      const parts = name.split('_');
      res[name] = { 
        displayName: parts[3] ? `${parts[2]} VS ${parts[3]}` : parts[2] || 'UNIT' 
      };
    }
  });
  return res;
});

const nodesCount = computed(() => Object.keys(matchNodes.value).length);

// --- 纯前端发送逻辑 (替代 WebSocket) ---
const handleSend = () => {
  const val = inputMsg.value.trim();
  if (!val) return;

  // 直接推送到本地弹幕组件，不再经过后端
  if (danmakuRef.value) {
    danmakuRef.value.push(val, 'USER');
  }
  
  inputMsg.value = '';
};

// --- 直播流播放逻辑 ---
const openVideo = async (id) => {
  activeStream.value = id;
  
  await nextTick();
  // 建立 SRS 播放器
  player = RoboCupStreamer();
  
  // 这里的 IP 建议统一使用你之前的 SRS IP
  const srsIp = "192.168.31.203"; 
  const url = `webrtc://${srsIp}/live/${id}`;
  const apiUrl = `http://${srsIp}:1985/rtc/v1/play/`;

  try {
    await player.play(url, apiUrl);
    const videoElement = document.getElementById('rtc_media_player');
    if (videoElement) {
      videoElement.srcObject = player.stream;
      videoElement.play();
    }
    // 成功进入时自动发一条系统弹幕
    danmakuRef.value?.push(`LINK_ESTABLISHED: NODE_${id.toUpperCase()}`, 'SYS');
  } catch (err) {
    console.error("STREAM_PLAY_FAILED", err);
    danmakuRef.value?.push("CONNECTION_TIMEOUT: SIGNAL_LOST", "SYS");
  }
};

const closeVideo = () => {
  if (player) player.close();
  activeStream.value = '';
};

// --- 数据轮询：刷新流列表 ---
const fetchStatus = async () => {
  try {
    const res = await fetch('/api/v1/srs/streams');
    const data = await res.json();
    // 兼容不同的 SRS 版本返回格式
    allStreams.value = data.streams || data; 
  } catch (e) {
    console.warn("SRS_SERVER_OFFLINE");
  }
};

// --- 生命周期 ---
onMounted(() => {
  statusTimer = setInterval(fetchStatus, 3000);
  fetchStatus();

  // // 模拟一些自动飘过的系统数据，增加氛围感
  // autoSystemDmTimer = setInterval(() => {
  //   if (activeStream.value && danmakuRef.value) {
  //     const msgs = ['PACKET_LOSS: 0%', 'BUFFER_STATE: OPTIMAL', 'ENCODING: H.264_STABLE'];
  //     const randomMsg = msgs[Math.floor(Math.random() * msgs.length)];
  //     danmakuRef.value.push(randomMsg, 'SYSTEM');
  //   }
  // }, 10000);
});

onUnmounted(() => {
  clearInterval(statusTimer);
  clearInterval(autoSystemDmTimer);
  closeVideo();
});
</script>

<style scoped>
/* 核心布局 */
.monitor-view { height: 100vh; padding: 20px; color: var(--theme-color); display: flex; flex-direction: column; background: #0a0a0a; font-family: monospace; }
.header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid; padding-bottom: 10px; margin-bottom: 20px; }
.back-btn { background: transparent; color: inherit; border: 1px solid; cursor: pointer; padding: 5px 15px; font-weight: bold; }

/* 节点卡片 */
.nodes-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; }
.group-card { background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.1); padding: 20px; cursor: pointer; position: relative; overflow: hidden; }
.group-card:hover { border-color: var(--theme-color); background: rgba(255,255,255,0.08); transform: translateY(-2px); }
.group-title { font-size: 1.2rem; font-weight: bold; margin-bottom: 8px; }
.group-id { font-size: 10px; opacity: 0.4; }
.btn { margin-top: 15px; font-size: 12px; border: 1px solid; width: fit-content; padding: 2px 8px; }

/* 播放弹窗 */
.modal { position: fixed; inset: 0; background: rgba(0,0,0,0.92); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 40px; }
.window { width: 100%; max-width: 1000px; border: 1px solid var(--theme-color); background: #000; box-shadow: 0 0 30px rgba(0,0,0,0.5); }
.bar { background: var(--theme-color); color: #000; padding: 8px 15px; display: flex; justify-content: space-between; font-weight: bold; align-items: center; }
.close-btn { background: #000; color: #fff; border: none; padding: 4px 10px; cursor: pointer; font-size: 12px; }

.video-body { padding: 15px; }
.player-container { position: relative; width: 100%; background: #111; border: 1px solid #333; line-height: 0; }
video { width: 100%; max-height: 70vh; }

/* 终端输入框 */
.console-box { margin-top: 15px; display: flex; background: #050505; border: 1px solid #333; padding: 8px 15px; align-items: center; }
.prompt { color: var(--theme-color); margin-right: 12px; font-weight: bold; font-size: 14px; }
.console-box input { flex: 1; background: transparent; border: none; color: #fff; outline: none; font-family: inherit; font-size: 16px; }
.exec-btn { background: var(--theme-color); color: #000; border: none; padding: 5px 15px; font-weight: bold; cursor: pointer; margin-left: 10px; }

/* 扫描动画 */
.scanner { width: 50px; height: 50px; border: 2px solid; border-top-color: transparent; border-radius: 50%; animation: spin 1s infinite linear; margin: 50px auto; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>