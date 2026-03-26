<template>
  <div class="cyber-page">
    <div class="bg-lines"></div>
    
    <div class="v-ambient-scan"></div>

    <div class="upload-container">
      <div class="card-header-line"></div>
      
      <div class="header-section">
        <h1 class="glitch-title" data-text="MINIO_CORE_UPLOADER">MINIO_CORE_UPLOADER</h1>
        <p class="system-path">DIRECTORY: /AUST/INTERNAL/ASSETS</p>
      </div>

      <div 
        class="upload-area" 
        :class="{ 'is-uploading': isUploading, 'is-dragover': isDragOver }" 
        @click="!isUploading && $refs.fileInput.click()"
        @dragover.prevent="isDragOver = true" 
        @dragleave.prevent="isDragOver = false" 
        @drop.prevent="handleDrop"
      >
        <input type="file" ref="fileInput" hidden @change="handleFileChange" />

        <div v-if="!isUploading" class="prompt-box">
          <div class="upload-icon">📡</div>
          <p class="main-prompt">> 准备就绪，等待数据注入...</p>
          <p class="sub-prompt">[ 点击或拖拽文件至感应区 ]</p>
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
        <p class="res-msg">{{ uploadResult.code === 200 ? '文件同步完成: ' + uploadResult.fileName : '错误报告: ' + uploadResult.msg }}</p>
      </div>

      <button class="exit-btn" @click="$router.push('/')">
        <span>[ TERMINATE_UPLOADER ]</span>
      </button>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import gsap from 'gsap';

const router = useRouter();

// 状态变量
const isUploading = ref(false);    // 是否正在上传
const isDragOver = ref(false);     // 是否正在拖拽文件经过
const progress = ref(0);           // 上传进度 (0-100)
const uploadResult = ref(null);    // 上传后的服务器响应
const fileInput = ref(null);       // 绑定 input 元素

// 页面入场动画
onMounted(() => {
  // 让上传卡片从中心微缩放并淡入，与登录页的转场衔接
  gsap.from(".upload-container", {
    duration: 1.2,
    scale: 0.9,
    opacity: 0,
    y: 20,
    ease: "power3.out"
  });

  // 标题文字微弱闪烁效果
  gsap.from(".glitch-title", {
    duration: 1.5,
    opacity: 0,
    delay: 0.5
  });
});

/**
 * 处理文件选择
 */
const handleFileChange = (e) => {
  const file = e.target.files[0];
  if (file) executeUpload(file);
};

/**
 * 处理拖拽上传
 */
const handleDrop = (e) => {
  isDragOver.value = false;
  const file = e.dataTransfer.files[0];
  if (file) executeUpload(file);
};

/**
 * 执行上传逻辑
 */
const executeUpload = async (file) => {
  // 1. 初始化状态
  isUploading.value = true;
  uploadResult.value = null;
  progress.value = 0;

  // 2. 准备数据
  const formData = new FormData();
  formData.append('file', file);

  try {
    // 3. 发送请求 (使用相对路径以配合 Vite Proxy)
    const res = await axios.post('/file/upload', formData, {
      onUploadProgress: (progressEvent) => {
        // 计算百分比并赋值给 progress
        if (progressEvent.total) {
          progress.value = Math.round((progressEvent.loaded * 100) / progressEvent.total);
        }
      }
    });

    // 4. 处理成功结果
    uploadResult.value = res.data; 
    // 如果 code 为 200，说明上传成功
  } catch (err) {
    // 5. 处理异常
    console.error("Upload Error:", err);
    uploadResult.value = { 
      code: 500, 
      msg: err.response?.data?.msg || "无法连接到 AUST 后端服务 [8081]" 
    };
  } finally {
    // 6. 结束上传状态
    isUploading.value = false;
  }
};

/**
 * 退出终端
 */
const exitTerminal = () => {
  // 可以在这里加入 maskRef.value.play() 如果你在这个页面也引入了 TransitionMask
  router.push('/');
};
</script>

<style scoped>
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