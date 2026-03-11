<template>
  <div class="cyber-page">
    <div class="bg-lines"></div>
    
    <div class="login-card">
      <div class="card-header-line"></div>
      
      <div class="logo-section">
        <div class="dino-glow">🦖</div>
        <h2 class="title">DINO TERMINAL</h2>
        <p class="subtitle">SYSTEM AUTHENTICATION REQUIRED</p>
      </div>

      <div class="form-body">
        <div class="cyber-input">
          <input v-model="form.username" type="text" placeholder="USER_ID" required />
          <div class="input-bar"></div>
        </div>

        <div class="cyber-input">
          <input v-model="form.password" type="password" placeholder="ACCESS_CODE" required />
          <div class="input-bar"></div>
        </div>

        <button @click="startAuth" :class="{ 'loading': isAuth }" class="auth-btn">
          <span v-if="!isAuth">[ INITIALIZE_SESSION ]</span>
          <span v-else>VERIFYING...</span>
        </button>
      </div>

      <div class="footer">
        <span class="id-tag">UID: {{ uniqueId || 'NULL' }}</span>
        <a href="#" @click.prevent="generateNewId" class="reg-link">GENERATE_NEW_IDENTITY</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';

const emit = defineEmits(['login-success']);
const isAuth = ref(false);
const uniqueId = ref('');

const form = reactive({
  username: '',
  password: ''
});

// 模拟生成唯一ID
const generateNewId = () => {
  uniqueId.value = 'DINO-' + Math.random().toString(36).substr(2, 9).toUpperCase();
};

const startAuth = () => {
  if(!form.username) return alert('请输入用户ID');
  isAuth.value = true;
  
  // 模拟根据 ID 生成 Token 的延迟
  setTimeout(() => {
    const token = `TK_${btoa(form.username)}_${Date.now()}`;
    localStorage.setItem('dino_token', token);
    isAuth.value = false;
    
    // 登录成功，告诉父组件隐藏登录页
    emit('login-success', token);
  }, 2000);
};
</script>

<style scoped>
/* 核心容器：深色赛博背景 */
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

/* 背景流光线条动画 */
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

/* 登录卡片：霓虹边框 + 毛玻璃 */
.login-card {
  position: relative;
  width: 400px;
  padding: 40px;
  background: rgba(10, 15, 20, 0.8);
  border: 1px solid #00f3ff;
  box-shadow: 0 0 20px rgba(0, 243, 255, 0.2), inset 0 0 10px rgba(0, 243, 255, 0.1);
  backdrop-filter: blur(15px);
  text-align: center;
}

.card-header-line {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 4px;
  background: linear-gradient(90deg, #00f3ff, #ff00ff);
}

.dino-glow {
  font-size: 60px;
  filter: drop-shadow(0 0 15px #00f3ff);
  margin-bottom: 10px;
}

.title {
  color: #00f3ff;
  letter-spacing: 5px;
  margin: 10px 0;
  text-shadow: 0 0 10px #00f3ff;
}

.subtitle {
  color: #555;
  font-size: 10px;
  margin-bottom: 30px;
}

/* 输入框：极简线框风格 */
.cyber-input {
  position: relative;
  margin-bottom: 25px;
}

.cyber-input input {
  width: 100%;
  background: transparent;
  border: none;
  border-bottom: 1px solid #333;
  padding: 10px 5px;
  color: #00f3ff;
  outline: none;
  font-size: 16px;
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

/* 按钮：霓虹扫描效果 */
.auth-btn {
  width: 100%;
  padding: 15px;
  background: transparent;
  border: 1px solid #ff00ff;
  color: #ff00ff;
  cursor: pointer;
  font-weight: bold;
  transition: 0.3s;
  position: relative;
  overflow: hidden;
}

.auth-btn:hover {
  background: rgba(255, 0, 255, 0.1);
  box-shadow: 0 0 15px rgba(255, 0, 255, 0.5);
  text-shadow: 0 0 5px #ff00ff;
}

.auth-btn.loading {
  border-color: #555;
  color: #555;
  cursor: wait;
}

.footer {
  margin-top: 30px;
  display: flex;
  justify-content: space-between;
  font-size: 10px;
}

.id-tag { color: #00f3ff; opacity: 0.6; }
.reg-link { color: #ff00ff; text-decoration: none; }
.reg-link:hover { text-decoration: underline; }
</style>