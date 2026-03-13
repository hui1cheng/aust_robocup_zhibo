import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// 统一使用当前最新的局域网 IP
const SERVER_IP = '192.168.31.203';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) },
  },
  server: {
      //  https: true, // 开启 HTTPS
    host: '0.0.0.0',
    port: 5173, // [改动] 换成 Vite 默认的 5173，避开 SRS 的 8080
    proxy: {
      // 找 Java 后端处理业务和文件
      '/api': {
        target: `http://${SERVER_IP}:8081`,
        changeOrigin: true
      },
      '/file': {
        target: `http://${SERVER_IP}:8081`,
        changeOrigin: true
      },
      // 找 SRS 处理 WebRTC 信令
      '/rtc': {
        target: `http://${SERVER_IP}:1985`,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/rtc/, '/rtc/v1/play') // 建议加上，明确信令路径
      },
      // 3. 找 SRS 的 HTTP 服务器 (如果你需要访问 SRS 内部的静态资源)
      // 这就是你改成的 8082 端口
      '/srs-static': {
        target: `http://192.168.31.203:8082`,
        changeOrigin: true
      }
    }
  }
})