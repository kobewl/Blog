import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { useUserStore } from '../stores/user'
import type { ApiResponse } from '../types'

// 创建 axios 实例
const instance = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器
instance.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      // 确保 token 格式正确
      config.headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求配置错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果响应成功
    if (res.code === 200) {
      return res.data
    }
    
    // 处理业务错误
    ElMessage.error(res.message || '操作失败')
    return Promise.reject(new Error(res.message || '操作失败'))
  },
  error => {
    console.error('请求错误:', error.response || error)
    
    if (error.response) {
      const { status, data } = error.response
      
      // 处理特定的错误状态
      switch (status) {
        case 401:
          // 处理token相关错误
          if (data?.message?.includes('token') || 
              data?.message?.includes('登录') || 
              data?.message?.includes('过期')) {
            const userStore = useUserStore()
            userStore.clearToken()
            router.push('/login')
            ElMessage.error('登录已过期，请重新登录')
          } else {
            ElMessage.error(data?.message || '未授权的访问')
          }
          break
        case 403:
          ElMessage.error(data?.message || '没有权限执行此操作')
          break
        case 404:
          ElMessage.error(data?.message || '请求的资源不存在')
          break
        case 500:
          ElMessage.error(data?.message || '服务器错误，请稍后重试')
          break
        default:
          ElMessage.error(data?.message || '请求失败，请重试')
      }
    } else if (error.message.includes('timeout')) {
      ElMessage.error('请求超时，请重试')
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

// 封装请求方法
const request = {
  get: <T>(url: string, config?: AxiosRequestConfig) => {
    return instance.get<any, T>(url, config)
  },
  post: <T>(url: string, data?: any, config?: AxiosRequestConfig) => {
    return instance.post<any, T>(url, data, config)
  },
  put: <T>(url: string, data?: any, config?: AxiosRequestConfig) => {
    return instance.put<any, T>(url, data, config)
  },
  delete: <T>(url: string, config?: AxiosRequestConfig) => {
    return instance.delete<any, T>(url, config)
  }
}

export default request 