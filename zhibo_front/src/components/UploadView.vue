<template>
  <div class="cyber-page">
    <div class="bg-lines"></div>
    <div class="v-ambient-scan"></div>

    <div class="upload-container">
      <div class="card-header-line"></div>
      
      <div class="header-section magnetic-target">
        <h1 class="glitch-title" data-text="MINIO_CORE_UPLOADER">MINIO_CORE_UPLOADER</h1>
        <p class="system-path">DIRECTORY: /AUST/INTERNAL/ASSETS</p>
      </div>

      <div 
        class="upload-area" 
        :class="{ 
          'is-uploading': isUploading, 
          'is-dragover': isDragOver,
          'is-locked': isCooldown 
        }" 
        @click="handleAreaClick"
        @dragover.prevent="!isCooldown && (isDragOver = true)" 
        @dragleave.prevent="isDragOver = false" 
        @drop.prevent="handleDrop"
      >
        <input type="file" ref="fileInput" hidden @change="handleFileChange" />

        <div v-if="!isUploading" class="prompt-box">
          <div class="upload-icon">{{ isCooldown ? '⏳' : '📡' }}</div>
          <p class="main-prompt">
            {{ isCooldown ? `> 系统冷却中: ${cooldownText}` : '> 准备就绪，等待数据注入...' }}
          </p>
          <p class="sub-prompt">
            {{ isCooldown ? '[ 物理链路已切断 ]' : '[ 点击或拖拽文件至感应区 ]' }}
          </p>
        </div>

        <div v-else class="progress-box">
          <div class="loading-ring"></div>
          <div class="bar-wrapper">
            <div class="progress-fill" :style="{ width: progress + '%' }"></div>
          </div>
          <p class="status-text">SYNCING_DATA: {{ progress }}%</p>
        </div>
      </div>

      <div class="console-output" v-if="uploadResult">
        <div :class="['res-tag', uploadResult.code === 200 ? 'tag-ok' : 'tag-err']">
          {{ uploadResult.code === 200 ? 'SUCCESS' : 'FAILURE' }}
        </div>
        <p class="res-msg">
          {{ uploadResult.code === 200 ? '文件同步完成: ' + uploadResult.fileName : '错误报告: ' + uploadResult.msg }}
        </p>
      </div>

      <button class="exit-btn" @click="$router.push('/')">
        <span>[ TERMINATE_UPLOADER ]</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import gsap from 'gsap';

const router = useRouter();

// --- 状态变量 ---
const isUploading = ref(false);
const isDragOver = ref(false);
const progress = ref(0);
const uploadResult = ref(null);
const fileInput = ref(null);

// --- 冷却逻辑 ---
const COOLDOWN_MS = 5 * 60 * 1000; // 5分钟
const lastUploadTime = ref(Number(localStorage.getItem('LAST_UPLOAD_TS')) || 0);
const currentTime = ref(Date.now());
let timer = null;

// 每秒更新当前时间，驱动倒计时
onMounted(() => {
  timer = setInterval(() => {
    currentTime.value = Date.now();
  }, 1000);

  // 入场动画
  gsap.from(".upload-container", { duration: 1.2, scale: 0.9, opacity: 0, y: 20, ease: "power3.out" });
});

onUnmounted(() => {
  if (timer) clearInterval(timer);
});

// 计算属性：是否处于冷却中
const isCooldown = computed(() => {
  return (currentTime.value - lastUploadTime.value) < COOLDOWN_MS;
});

// 计算属性：倒计时文字内容
const cooldownText = computed(() => {
  const remaining = Math.max(0, COOLDOWN_MS - (currentTime.value - lastUploadTime.value));
  const m = Math.floor(remaining / 60000);
  const s = Math.floor((remaining % 60000) / 1000);
  return `${m}m ${s}s`;
});

// --- 事件处理 ---
const handleAreaClick = () => {
  if (isUploading.value || isCooldown.value) return;
  fileInput.value.click();
};

const handleFileChange = (e) => {
  const file = e.target.files[0];
  if (file) executeUpload(file);
};

const handleDrop = (e) => {
  isDragOver.value = false;
  if (isCooldown.value) return;
  const file = e.dataTransfer.files[0];
  if (file) executeUpload(file);
};

// --- 核心上传函数 ---
const executeUpload = async (file) => {
  // 二次安全校验
  if (isCooldown.value) return;

  isUploading.value = true;
  uploadResult.value = null;
  progress.value = 0;

  const formData = new FormData();
  formData.append('file', file);

  try {
    const res = await axios.post('/file/upload', formData, {
      onUploadProgress: (p) => {
        if (p.total) progress.value = Math.round((p.loaded * 100) / p.total);
      }
    });

    uploadResult.value = res.data;

    if (res.data.code === 200) {
      // 记录成功时间并持久化
      const now = Date.now();
      lastUploadTime.value = now;
      localStorage.setItem('LAST_UPLOAD_TS', now.toString());
    }
  } catch (err) {
    console.error("Upload Error:", err);
    uploadResult.value = { 
      code: 500, 
      msg: err.response?.data?.msg || "无法连接到 AUST 后端服务 [8081]" 
    };
  } finally {
    isUploading.value = false;
  }
};
</script>

<style scoped>
/* 在原有样式基础上，补充锁定状态的样式 */
.upload-area.is-locked {
  cursor: not-allowed;
  border-color: rgba(255, 0, 0, 0.3);
  background: rgba(255, 0, 0, 0.05);
}

.upload-area.is-locked .main-prompt {
  color: #ff5555;
  text-shadow: 0 0 10px rgba(255, 85, 85, 0.5);
}

.upload-area.is-locked .upload-icon {
  filter: grayscale(1);
  opacity: 0.6;
}
/* 1. 基础容器：保持与登录页一致的深色底 */
.cyber-page {
  position: fixed; inset: 0; background: #050505;
  display: flex; justify-content: center; align-items: center;
  overflow: hidden; font-family: 'Courier New', monospace;
}

/* 2. 背景与扫描线逻辑 */
.bg-lines {
  position: absolute; width: 120%; height: 120%;
  background-image: linear-gradient(0deg, transparent 24%, rgba(0, 243, 255, 0.03) 25%, transparent 27%),
                    linear-gradient(90deg, transparent 24%, rgba(0, 243, 255, 0.03) 25%, transparent 27%);
  background-size: 60px 60px;
  animation: bg-float 20s linear infinite;
}
@keyframes bg-float {
  0% { transform: translate(-5%, -5%); }
  100% { transform: translate(0%, 0%); }
}

.v-ambient-scan {
  position: absolute; inset: 0; pointer-events: none;
  background: linear-gradient(to bottom, transparent, rgba(0, 243, 255, 0.05), transparent);
  height: 100px; animation: scan 8s linear infinite;
}
@keyframes scan { from { top: -100px; } to { top: 100%; } }

/* 3. 上传卡片：高斯模糊 + 青色边框 */
.upload-container {
  position: relative; width: 550px; padding: 40px;
  background: rgba(10, 15, 20, 0.9);
  border: 1px solid rgba(0, 243, 255, 0.3);
  box-shadow: 0 0 30px rgba(0, 243, 255, 0.1);
  backdrop-filter: blur(15px);
  z-index: 10;
}

.card-header-line {
  position: absolute; top: 0; left: 0; width: 100%; height: 2px;
  background: linear-gradient(to right, #00f3ff, #ff00ff);
}

.glitch-title {
  color: #00f3ff; font-size: 24px; text-align: center;
  text-shadow: 0 0 10px rgba(0, 243, 255, 0.5);
}

.system-path {
  color: rgba(0, 243, 255, 0.4); font-size: 10px; text-align: center; margin-top: 5px;
}

/* 4. 上传感应区：交互感增强 */
.upload-area {
  margin-top: 30px; height: 180px;
  border: 1px dashed rgba(0, 243, 255, 0.3);
  display: flex; justify-content: center; align-items: center;
  transition: 0.3s; cursor: pointer;
}
.upload-area:hover, .is-dragover {
  background: rgba(0, 243, 255, 0.05);
  border-color: #00f3ff;
  box-shadow: inset 0 0 20px rgba(0, 243, 255, 0.1);
}

.main-prompt { color: #00f3ff; margin-bottom: 8px; }
.sub-prompt { color: rgba(255, 255, 255, 0.3); font-size: 12px; }

/* 进度条：青色流光 */
.bar-wrapper {
  width: 300px; height: 4px; background: rgba(255, 255, 255, 0.1);
  margin: 15px auto; overflow: hidden;
}
.progress-fill {
  height: 100%; background: #00f3ff;
  box-shadow: 0 0 10px #00f3ff; transition: width 0.3s;
}

/* 5. 退出按钮：紫色调 */
.exit-btn {
  margin-top: 30px; width: 100%; background: transparent;
  border: 1px solid #ff00ff; color: #ff00ff; padding: 12px;
  cursor: pointer; transition: 0.3s; font-weight: bold;
}
.exit-btn:hover {
  background: rgba(255, 0, 255, 0.1);
  box-shadow: 0 0 15px rgba(255, 0, 255, 0.3);
}

/* 控制台输出 */
.console-output {
  margin-top: 25px; padding: 15px; background: rgba(0, 0, 0, 0.4);
  border-left: 2px solid #00f3ff;
}
.res-tag { font-size: 10px; margin-bottom: 5px; font-weight: bold; }
.tag-ok { color: #00f3ff; }
.tag-err { color: #ff00ff; }
.res-msg { color: #fff; font-size: 13px; opacity: 0.8; }
</style>