<template>
  <div class="cyber-page">
    <div class="bg-lines"></div>
    <div class="v-ambient-scan"></div>

    <TransitionMask ref="maskRef" />
    
    <div class="login-card-wrapper">
      <div class="card-corner top-left"></div>
      <div class="card-corner top-right"></div>
      <div class="card-corner bottom-left"></div>
      <div class="card-corner bottom-right"></div>

      <div class="login-card">
        <div class="card-header-line"></div>
        
        <div class="logo-section">
          <CyberDino class="compact-logo" />
          <h2 class="compact-title">安徽理工大学</h2>
          <p class="compact-subtitle">INTELLIGENT TERMINAL</p>
        </div>

        <div class="form-body">
          <div class="cyber-input">
            <input v-model="form.username" type="text" placeholder="STUDENT_ID" required />
            <div class="input-bar"></div>
          </div>
          <div class="cyber-input">
            <input v-model="form.password" type="password" placeholder="ACCESS_CODE" @keyup.enter="startAuth" required />
            <div class="input-bar"></div>
          </div>

          <div class="btn-group">
            <button @click="startAuth" :class="{ 'loading': isAuth }" class="auth-btn main-btn">
              <span>{{ isAuth ? 'VERIFYING...' : '[ LOGIN ]' }}</span>
            </button>
            <button @click="goToUpload" class="auth-btn upload-link">
              <span>[ UPLOAD ]</span>
            </button>
          </div>
        </div>

        <div class="footer">
          <span class="id-tag">SECURED</span>
          <a href="#" @click.prevent="resetFields" class="reg-link">[ RESET ]</a>
        </div>
      </div>
    </div>
    
    <div class="v-logs">
      <div v-for="i in 3" :key="i" class="log-item">>> SYSLOG_RECV: OK</div>
    </div>
  </div>
</template>
<script setup>
/* 此部分逻辑代码保持不变，请确保引入了必要的组件和函数 */
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import gsap from 'gsap';
import TransitionMask from './TransitionMask.vue';
import CyberDino from './CyberDino.vue';
const router = useRouter();
const isAuth = ref(false);
const maskRef = ref(null);

const form = reactive({
  username: '2024305279',
  password: '123456'
});

onMounted(() => {
  // 页面入场动画保持轻微上浮感
  gsap.from(".login-card", {
    duration: 1.2,
    y: 20,
    opacity: 0,
    ease: "power2.out"
  });
});

const startAuth = () => {
  if (!form.username || !form.password) return;
  isAuth.value = true;
  setTimeout(() => {
    if (form.username === '2024305279') {
      maskRef.value.play(() => { router.push('/House'); });
    } else {
      isAuth.value = false;
      alert('验证失败');
    }
  }, 1000);
};

const goToUpload = () => {
  maskRef.value.play(() => { router.push('/UploadView'); });
};

const resetFields = () => { form.username = ''; form.password = ''; };
</script>
<style scoped>


/* 1. 确保最外层容器严格锁定屏幕尺寸，不产生滚动条 */
.cyber-page {
  position: fixed; 
  top: 0; 
  left: 0; 
  width: 100vw; 
  height: 100vh;
  background: #000;
  display: flex;         /* 使用 Flex */
  justify-content: center; /* 水平居中 */
  align-items: center;     /* 垂直居中 */
  overflow: hidden;
}

/* 2. 修正包装器：使用 fixed 配合 50% 定位，彻底无视周围干扰 */
.login-card-wrapper {
  position: fixed; /* 改为 fixed */
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%); /* 核心：强制绝对居中 */
  width: 340px;
  z-index: 100; /* 提高层级，确保在所有背景和干扰层之上 */
}

/* 3. 修正角标的定位（基于 wrapper） */
.card-corner {
  position: absolute;
  width: 15px; 
  height: 15px;
  border: 2px solid #00f3ff;
  z-index: 11;
  pointer-events: none; /* 防止遮挡点击 */
}
/* 稍微往外弹一点，增加包裹感 */
.top-left     { top: -15px; left: -15px; border-right: none; border-bottom: none; }
.top-right    { top: -15px; right: -15px; border-left: none; border-bottom: none; }
.bottom-left  { bottom: -15px; left: -15px; border-right: none; border-top: none; }
.bottom-right { bottom: -15px; right: -15px; border-left: none; border-top: none; }

@keyframes corner-pulse {
  from { opacity: 0.3; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1.1); }
}

/* 按钮与输入框（保持紧凑版） */
.logo-section { display: flex; flex-direction: column; align-items: center; margin-bottom: 10px; }
.compact-logo { transform: scale(0.6); margin-bottom: -20px; }
.compact-title { color: #00f3ff; font-size: 18px; margin: 0; text-shadow: 0 0 10px #00f3ff; }
.compact-subtitle { color: rgba(0, 243, 255, 0.4); font-size: 9px; }

.cyber-input { position: relative; margin-bottom: 12px; }
.cyber-input input {
  width: 100%; background: transparent; border: none;
  border-bottom: 1px solid #222; padding: 8px 5px;
  color: #00f3ff; outline: none; font-size: 13px;
}

.btn-group { display: flex; gap: 10px; margin-top: 10px; }
.auth-btn {
  flex: 1; padding: 10px; background: transparent; 
  border: 1px solid #00f3ff; color: #00f3ff; font-size: 11px;
  cursor: pointer; transition: 0.3s;
}
.upload-link { border-color: #ff00ff; color: #ff00ff; }

/* 装饰背景保持 */
.bg-lines {
  position: absolute; inset: -50%;
  background-image: linear-gradient(rgba(0, 243, 255, 0.03) 1px, transparent 1px),
                    linear-gradient(90deg, rgba(0, 243, 255, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: bg-move 20s linear infinite;
}
@keyframes bg-move { from { transform: translate(0,0); } to { transform: translate(50px, 50px); } }
</style>