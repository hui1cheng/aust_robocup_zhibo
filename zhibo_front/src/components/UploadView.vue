<template>
  <div class="terminal-page">
    <div class="scanline"></div>
    <div class="noise"></div>

    <div class="upload-container">
      <div class="glitch-title" data-text="MINIO_UPLOADER">MINIO_UPLOADER</div>
      <p class="system-path">PATH: /AUST/SYSTEM/UPLOADS</p>

      <div 
        class="upload-area" 
        :class="{ 'is-uploading': isUploading }"
        @click="$refs.fileInput.click()"
        @dragover.prevent="isDragOver = true"
        @dragleave.prevent="isDragOver = false"
        @drop.prevent="handleDrop"
      >
        <input 
          type="file" 
          ref="fileInput" 
          hidden 
          @change="handleFileChange" 
        />

        <div v-if="!isUploading" class="prompt-text">
          <span class="blink-cursor">></span> 准备就绪...
          <br />
          <span class="sub-text">[ 点击或拖拽文件至此区域 ]</span>
        </div>

        <div v-else class="progress-box">
          <div class="loading-spinner"></div>
          <div class="bar-container">
            <div class="progress-bar" :style="{ width: progress + '%' }"></div>
          </div>
          <p class="percent-text">UPLOADING: {{ progress }}%</p>
        </div>
      </div>

      <div class="console-output" v-if="uploadResult">
        <p class="success" v-if="uploadResult.code === 200">
          [ SUCCESS ] 文件已同步至 MinIO: {{ uploadResult.fileName }}
        </p>
        <p class="error" v-else>
          [ FAILURE ] 错误信息: {{ uploadResult.msg }}
        </p>
      </div>

      <button class="return-btn" @click="$router.push('/')">EXIT_TERMINAL</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const isUploading = ref(false);
const isDragOver = ref(false);
const progress = ref(0);
const uploadResult = ref(null);

const handleFileChange = (e) => {
  const file = e.target.files[0];
  if (file) executeUpload(file);
};

const handleDrop = (e) => {
  isDragOver.value = false;
  const file = e.dataTransfer.files[0];
  if (file) executeUpload(file);
};

const executeUpload = async (file) => {
  isUploading.value = true;
  uploadResult.value = null;
  progress.value = 0;

  const formData = new FormData();
  formData.append('file', file);

  try {
    const res = await axios.post('http://localhost:8081/file/upload', formData, {
      onUploadProgress: (p) => {
        progress.value = Math.round((p.loaded * 100) / p.total);
      }
    });
    uploadResult.value = res.data;
  } catch (err) {
    uploadResult.value = { code: 500, msg: "无法连接到 8081 端口" };
  } finally {
    isUploading.value = false;
  }
};
</script>

<style scoped>
.terminal-page {
  background: #000;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #00ff41; /* 经典的黑客绿 */
  font-family: 'Courier New', monospace;
  position: relative;
  overflow: hidden;
}

/* 扫描线动画 */
.scanline {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(rgba(18, 16, 16, 0) 50%, rgba(0, 0, 0, 0.25) 50%), linear-gradient(90deg, rgba(255, 0, 0, 0.06), rgba(0, 255, 0, 0.02), rgba(0, 0, 255, 0.06));
  background-size: 100% 4px, 3px 100%;
  pointer-events: none;
  z-index: 2;
}

.upload-container {
  width: 600px;
  padding: 40px;
  border: 1px solid #00ff41;
  background: rgba(0, 20, 0, 0.8);
  box-shadow: 0 0 20px rgba(0, 255, 65, 0.2);
  z-index: 10;
}

.glitch-title {
  font-size: 2rem;
  font-weight: bold;
  text-align: center;
  margin-bottom: 5px;
  position: relative;
}

.system-path {
  font-size: 10px;
  opacity: 0.6;
  text-align: center;
  margin-bottom: 30px;
}

.upload-area {
  height: 200px;
  border: 1px dashed #00ff41;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-area:hover {
  background: rgba(0, 255, 65, 0.05);
  box-shadow: inset 0 0 15px rgba(0, 255, 65, 0.2);
}

.blink-cursor {
  animation: blink 1s infinite;
}

@keyframes blink { 50% { opacity: 0; } }

.progress-bar {
  height: 10px;
  background: #00ff41;
  box-shadow: 0 0 10px #00ff41;
  transition: width 0.3s;
}

.bar-container {
  width: 300px;
  height: 10px;
  background: #111;
  margin: 15px 0;
}

.console-output {
  margin-top: 20px;
  padding: 10px;
  background: #001100;
  font-size: 13px;
  border-left: 2px solid #00ff41;
}

.success { color: #00ff41; }
.error { color: #ff003c; border-left-color: #ff003c; }

.return-btn {
  margin-top: 30px;
  width: 100%;
  background: transparent;
  border: 1px solid #00ff41;
  color: #00ff41;
  padding: 10px;
  cursor: pointer;
}

.return-btn:hover {
  background: #00ff41;
  color: #000;
}
</style>