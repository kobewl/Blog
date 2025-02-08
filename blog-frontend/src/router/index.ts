import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import Layout from '../layout/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/',
      component: Layout,
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/Home.vue'),
          meta: { title: '首页' }
        },
        {
          path: 'article',
          name: 'article',
          component: () => import('../views/article/index.vue'),
          meta: { title: '文章管理' }
        },
        {
          path: 'article/edit/:id?',
          name: 'article-edit',
          component: () => import('../views/article/edit.vue'),
          meta: { title: '编辑文章' }
        },
        {
          path: 'article/:id',
          name: 'article-detail',
          component: () => import('../views/article/detail.vue'),
          meta: { title: '文章详情' }
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('../views/profile/index.vue'),
          meta: { title: '个人中心' }
        }
      ]
    }
  ]
})

// 修改路由守卫
router.beforeEach(async (to, from, next) => {
  // 如果访问登录页面，直接放行
  if (to.path === '/login') {
    next()
    return
  }

  const userStore = useUserStore()
  const token = userStore.token

  // 如果没有token，重定向到登录页
  if (!token) {
    next('/login')
    return
  }

  // 如果有token但没有用户信息，尝试获取用户信息
  if (!userStore.userInfo) {
    try {
      await userStore.getUserInfo()
    } catch (error) {
      console.error('获取用户信息失败:', error)
      next('/login')
      return
    }
  }

  next()
})

export default router 