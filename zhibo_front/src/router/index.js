// 1. 导入必要的模块
import { createRouter, createWebHistory } from 'vue-router'
// 导入页面组件
import Login from '../components/Login.vue'
import LivePlayer from '../components/LivePlayer.vue'
import UploadView from '@/components/UploadView.vue'

// 2. 定义路由规则
const routes = [
  // 重定向：默认打开首页
  {
    path: '/',
    redirect: '/Login'
  },
  // 首页路由
  {
    path: '/LivePlaye',          // 路由路径
    name: 'LivePlayer',           // 路由名称（可选，用于编程式导航）
    component: LivePlayer,        // 对应组件
    meta: { title: '首页' } // 元信息（可选，用于设置页面标题、权限等）
  },
  {
    path: '/Login',          // 路由路径
    name: 'Login',           // 路由名称（可选，用于编程式导航）
    component: Login,        // 对应组件
    meta: { title: '登录' } // 元信息（可选，用于设置页面标题、权限等）
  },
  {
    path: '/UploadView',          // 路由路径
    name: 'UploadView',           // 路由名称（可选，用于编程式导航）
    component: UploadView,        // 对应组件
    meta: { title: '上传' } // 元信息（可选，用于设置页面标题、权限等）
  },
//   // 404路由（必须放在最后）
//   {
//     path: '/:pathMatch(.*)*', // 匹配所有未定义的路径
//     component: LivePlayer
//   }
]

// 3. 创建路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL), // HTML5 历史模式（无#）
  routes // 挂载路由规则
})

// 4. 全局路由守卫（可选，示例：设置页面标题）
router.beforeEach((to, from, next) => {
  // 如果路由有元信息标题，设置浏览器标题
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next() // 放行路由
})

// 5. 导出路由实例
export default router