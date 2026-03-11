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
    host: '0.0.0.0', // 允许通过局域网 IP 访问前端界面
    port: 8080,      // 设置前端运行端口
    proxy: {
      // 1. 代理 SRS 的 WebRTC 信令接口
      '/rtc': {
        target: `http://${TARGET_IP}:1985`,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/rtc/, '/rtc') 
      },
      // 2. 代理你的 Java 后端接口
      '/api': {
        target: `http://${TARGET_IP}:8081`,
        changeOrigin: true,
        // 如果你的 Java 后端没有 /api 前缀，可以取消下面的 rewrite 注释
        // rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})