import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

const SERVER_IP = '192.168.31.203';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) },
  },
  server: {
    allowedHosts: ['huicheng12.eu.cc'],
    host: '0.0.0.0',
    port: 5173, 
    proxy: {
      // 捕获所有以 /api 开头的请求并转给 Java 后端
      '/api': {
        target: `http://${SERVER_IP}:8081`,
        changeOrigin: true
      },
      // 捕获所有以 /file 开头的请求并转给 Java 后端
      '/file': {
        target: `http://${SERVER_IP}:8081`,
        changeOrigin: true,
        // 如果后端接口没有 /file 前缀，可能需要 rewrite，但通常 MinIO 适配层是需要的
        // rewrite: (path) => path.replace(/^\/file/, '') 
      },
      '/rtc': {
        target: `http://${SERVER_IP}:8082`,
        changeOrigin: true
      },
      '/srs-static': {
        target: `http://${SERVER_IP}:8082`,
        changeOrigin: true
      }
    }
  }
})