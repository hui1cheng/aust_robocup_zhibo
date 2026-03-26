<template>
  <div class="monitor-view" :style="`--theme-color: ${config.color}`">
    <header class="header">
      <button @click="$router.push('/house')" class="back-btn">&lt;&lt; BACK</button>
      <h2 class="room-title">SECTOR_{{ type.toUpperCase() }}</h2>
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
          <span>LIVE: {{ matchNodes[activeStream]?.displayName }}</span>
          <button @click="closeVideo">CLOSE_X</button>
        </div>
        <div class="video-body">
          <video id="rtc_media_player" autoplay controls muted></video>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { RoboCupStreamer } from '@/utils/srs.sdk.js';

const props = defineProps(['type']); // 接收路由参数 (2d, 3d, rescue)
const allStreams = ref({});
const activeStream = ref('');
let player = null;
let timer = null;

const config = computed(() => {
  const themes = { '2d': '#00f3ff', '3d': '#ff00ff', 'rescue': '#ff5500' };
  return { color: themes[props.type] || '#00f3ff' };
});

const matchNodes = computed(() => {
  const res = {};
  Object.keys(allStreams.value).forEach(name => {
    if (name.startsWith(`livestream_${props.type}`)) {
      const parts = name.split('_');
      res[name] = { displayName: parts[3] ? `${parts[2]} VS ${parts[3]}` : parts[2] || 'UNIT' };
    }
  });
  return res;
});

const nodesCount = computed(() => Object.keys(matchNodes.value).length);

const openVideo = async (id) => {
  activeStream.value = id;
  await nextTick();
  player = RoboCupStreamer();
  const host = window.location.hostname;
  await player.play(`webrtc://${host}/live/${id}`, `/rtc`);
  console.log(`webrtc://${host}/live/${id}`)
  document.getElementById('rtc_media_player').srcObject = player.stream;
};

const closeVideo = () => {
  if (player) player.close();
  activeStream.value = '';
};

const fetchStatus = async () => {
  try {
    const res = await fetch('/api/v1/srs/streams');
    allStreams.value = await res.json();
  } catch (e) { console.warn("SRS_ERROR"); }
};

onMounted(() => { timer = setInterval(fetchStatus, 3000); fetchStatus(); });
onUnmounted(() => { clearInterval(timer); closeVideo(); });
</script>

<style scoped>
.monitor-view { height: 100%; padding: 20px; color: var(--theme-color); display: flex; flex-direction: column; }
.header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid; padding-bottom: 10px; }
.back-btn { background: transparent; color: inherit; border: 1px solid; cursor: pointer; padding: 5px 15px; }

.nodes-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 20px; padding: 30px 0; }
.group-card { background: rgba(255,255,255,0.05); border: 1px solid; padding: 20px; cursor: pointer; transition: 0.2s; }
.group-card:hover { background: rgba(255,255,255,0.1); transform: scale(1.05); }
.group-title { font-weight: bold; margin-bottom: 10px; }
.group-id { font-size: 10px; opacity: 0.5; }

.modal { position: fixed; inset: 0; background: rgba(0,0,0,0.9); display: flex; align-items: center; justify-content: center; z-index: 100; }
.window { width: 80%; border: 1px solid; background: #000; }
.bar { background: var(--theme-color); color: #000; padding: 10px; display: flex; justify-content: space-between; font-weight: bold; }
.video-body { padding: 10px; }
video { width: 100%; display: block; }

.scanner { width: 40px; height: 40px; border: 2px solid; border-top-color: transparent; border-radius: 50%; animation: spin 1s infinite linear; margin: 40px auto; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>