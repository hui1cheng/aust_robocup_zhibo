<template>
  <div class="cyber-page">
    <div class="bg-lines"></div>
    
    <div class="login-card">
      <div class="card-header-line"></div>
      
      <div class="logo-section">
        <div class="dino-glow">🏫</div> 
        <h2 class="title">安徽理工大学</h2>
        <p class="subtitle">AUST INTELLIGENT TERMINAL</p>
      </div>

      <div class="form-body">
        <div class="cyber-input">
          <input 
            v-model="form.username" 
            type="text" 
            placeholder="STUDENT_ID (默认: 2024305279)" 
            required 
          />
          <div class="input-bar"></div>
        </div>

        <div class="cyber-input">
          <input 
            v-model="form.password" 
            type="password" 
            placeholder="ACCESS_CODE (默认: 123456)" 
            required 
          />
          <div class="input-bar"></div>
        </div>

        <button @click="startAuth" :class="{ 'loading': isAuth }" class="auth-btn">
          <span v-if="!isAuth">[ INITIALIZE_SESSION ]</span>
          <span v-else>VERIFYING_CREDENTIALS...</span>
        </button>

        <button @click="goToUpload" class="auth-btn upload-link">
          <span>[ FILE_TRANSFER_CHANNEL ]</span>
        </button>
      </div>

      <div class="footer">
        <span class="id-tag">NET_STATUS: SECURED</span>
        <a href="#" @click.prevent="resetFields" class="reg-link">RESET_FIELDS</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const emit = defineEmits(['login-success']);
const isAuth = ref(false);

const form = reactive({
  username: '2024305279',
  password: '123456'
});

const resetFields = () => {
  form.username = '';
  form.password = '';
};

// 登录验证
const startAuth = () => {
  if (!form.username || !form.password) return alert('请输入信息');
  isAuth.value = true;
  setTimeout(() => {
    if (form.username === '2024305279' && form.password === '123456') {
      router.push('/LivePlayer'); 
    } else {
      isAuth.value = false;
      alert('身份验证失败');
    }
  }, 1500);
};

// 直接跳转上传界面
const goToUpload = () => {
  router.push('/UploadView');
};
</script>

<style scoped>
/* --- 以下是原本的 CSS，完全未动 --- */
.cyber-page {
  position: fixed;
  top: 0; left: 0;
  width: 100vw; height: 100vh;
  background: #050505;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  overflow: hidden;
  font-family: 'Courier New', Courier, monospace;
}

.bg-lines {
  position: absolute;
  width: 200%; height: 200%;
  background-image: linear-gradient(0deg, transparent 24%, rgba(32, 255, 255, 0.05) 25%, rgba(32, 255, 255, 0.05) 26%, transparent 27%, transparent 74%, rgba(32, 255, 255, 0.05) 75%, rgba(32, 255, 255, 0.05) 76%, transparent 77%, transparent),
                    linear-gradient(90deg, transparent 24%, rgba(32, 255, 255, 0.05) 25%, rgba(32, 255, 255, 0.05) 26%, transparent 27%, transparent 74%, rgba(32, 255, 255, 0.05) 75%, rgba(32, 255, 255, 0.05) 76%, transparent 77%, transparent);
  background-size: 50px 50px;
  animation: bg-move 60s linear infinite;
}

@keyframes bg-move {
  from { transform: translate(-10%, -10%); }
  to { transform: translate(0%, 0%); }
}

.login-card {
  position: relative;
  width: 420px;
  padding: 40px;
  background: rgba(10, 15, 20, 0.85);
  border: 1px solid #00f3ff;
  box-shadow: 0 0 25px rgba(0, 243, 255, 0.2);
  backdrop-filter: blur(15px);
  text-align: center;
}

.card-header-line {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 4px;
  background: linear-gradient(90deg, #00f3ff, #4facfe);
}

.title {
  color: #00f3ff;
  letter-spacing: 2px;
  margin: 15px 0 5px 0;
  text-shadow: 0 0 15px rgba(0, 243, 255, 0.6);
  font-size: 28px;
}

.subtitle {
  color: #4facfe;
  font-size: 10px;
  margin-bottom: 30px;
  opacity: 0.7;
}

.cyber-input {
  position: relative;
  margin-bottom: 25px;
}

.cyber-input input {
  width: 100%;
  background: transparent;
  border: none;
  border-bottom: 1px solid #333;
  padding: 12px 5px;
  color: #00f3ff;
  outline: none;
  font-size: 14px;
}

.input-bar {
  position: absolute;
  bottom: 0; left: 50%;
  width: 0; height: 2px;
  background: #00f3ff;
  transition: 0.4s;
}

.cyber-input input:focus + .input-bar {
  width: 100%;
  left: 0;
  box-shadow: 0 0 10px #00f3ff;
}

/* 基础按钮样式 */
.auth-btn {
  width: 100%;
  padding: 15px;
  background: transparent;
  border: 1px solid #00f3ff;
  color: #00f3ff;
  cursor: pointer;
  font-weight: bold;
  transition: 0.3s;
  letter-spacing: 1px;
  margin-bottom: 15px; /* 增加间距 */
  font-family: inherit;
}

.auth-btn:hover {
  background: rgba(0, 243, 255, 0.1);
  box-shadow: 0 0 15px rgba(0, 243, 255, 0.4);
}

/* --- 特殊处理：上传按钮微调颜色以作区分 --- */
.upload-link {
  border-color: #ff00ff;
  color: #ff00ff;
  margin-bottom: 0; /* 最后一个按钮不需要下边距 */
}

.upload-link:hover {
  background: rgba(255, 0, 255, 0.1);
  box-shadow: 0 0 15px rgba(255, 0, 255, 0.4);
}

.auth-btn.loading {
  border-color: #444;
  color: #444;
}

.footer {
  margin-top: 30px;
  display: flex;
  justify-content: space-between;
  font-size: 10px;
}

.id-tag { color: #00f3ff; opacity: 0.5; }
.reg-link { color: #4facfe; text-decoration: none; }
</style>