import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// 你的局域网 IP
// const TARGET_IP = '10.49.249.230';
const TARGET_IP = '192.168.31.203';

export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
 server: {
    host: '0.0.0.0',
    port: 8080,
    // vite.config.js
proxy: {
  // 1. 找 Java 后端 (8081) 获取流列表等业务逻辑
  '/api': {
    target: `http://192.168.31.203:8081`,
    changeOrigin: true
  },
  // 2. 找 SRS API (1985) 处理 WebRTC 信令
  '/rtc': {
    target: `http://192.168.31.203:1985`,
    changeOrigin: true
  },
  // 3. 找 SRS 静态资源 (8080) 访问官方播放器
  // 注意：如果你的前端也占用了 8080，请确保 SRS 的 http_server 换成了别的端口（比如 8085）
  '/srs-static': {
    target: `http://192.168.31.203:8080`, 
    changeOrigin: true,
  }
}
  }
})