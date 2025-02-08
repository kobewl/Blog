import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/',
      name: 'Layout',
      component: () => import('../layout/index.vue'),
      children: [
        {
          path: '',
          name: 'Home',
          component: () => import('../views/Home.vue')
        },
        {
          path: 'article',
          name: 'Article',
          component: () => import('../views/article/index.vue')
        },
        {
          path: 'article/edit/:id?',
          name: 'ArticleEdit',
          component: () => import('../views/article/edit.vue')
        },
        {
          path: '/article/:id',
          name: 'ArticleDetail',
          component: () => import('../views/article/detail.vue')
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('../views/profile/index.vue')
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