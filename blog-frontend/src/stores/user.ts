import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User } from '../types'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<User | null>(null)
  const token = ref<string>(localStorage.getItem('token') || '')

  // 设置token的方法
  const setToken = (newToken: string) => {
    // 确保token格式正确
    const formattedToken = newToken.startsWith('Bearer ') ? newToken : `Bearer ${newToken}`
    token.value = formattedToken
    localStorage.setItem('token', formattedToken)
  }

  // 清除token和用户信息
  const clearToken = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  // 注册
  const register = async (data: { username: string; password: string; email: string }) => {
    try {
      const response = await request.post('/user/register', data)
      ElMessage.success('注册成功')
      return response
    } catch (error: any) {
      console.error('注册失败:', error)
      throw new Error(error?.response?.data?.message || '注册失败，请重试')
    }
  }

  // 登录
  const login = async (username: string, password: string) => {
    try {
      const response = await request.post('/user/login', { username, password })
      
      // 处理不同的返回格式
      const tokenValue = response?.token || response?.data?.token || response
      
      if (tokenValue) {
        setToken(tokenValue)
        console.log('Token已设置:', tokenValue)
        
        // 登录后立即获取用户信息
        await getUserInfo()
        return { success: true }
      }
      throw new Error('登录失败：未获取到token')
    } catch (error: any) {
      console.error('登录失败:', error)
      clearToken()
      throw new Error(error?.response?.data?.message || '登录失败，请重试')
    }
  }

  // 注销
  const logout = async () => {
    try {
      clearToken()
      ElMessage.success('已安全退出')
    } catch (error) {
      console.error('注销失败:', error)
      throw error
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      if (!token.value) {
        throw new Error('未登录状态')
      }
      
      const res = await request.get('/user/info')
      userInfo.value = res
      return res
    } catch (error: any) {
      console.error('获取用户信息失败:', error)
      // 如果是token相关错误，清除用户状态
      if (error?.response?.status === 401) {
        clearToken()
      }
      throw error
    }
  }

  // 更新用户信息
  const updateProfile = async (data: any) => {
    try {
      const res = await request.put('/user/update', data)
      await getUserInfo() // 更新后重新获取用户信息
      return res
    } catch (error) {
      console.error('更新用户信息失败:', error)
      throw error
    }
  }

  // 更新邮箱
  const updateEmail = async (email: string) => {
    try {
      await request.put('/user/update/email', null, { params: { email } })
      await getUserInfo() // 更新后重新获取用户信息
      ElMessage.success('邮箱更新成功')
    } catch (error) {
      console.error('更新邮箱失败:', error)
      throw error
    }
  }

  // 更新用户名
  const updateUsername = async (username: string) => {
    try {
      await request.put('/user/update/username', null, { params: { username } })
      await getUserInfo() // 更新后重新获取用户信息
      ElMessage.success('用户名更新成功')
    } catch (error) {
      console.error('更新用户名失败:', error)
      throw error
    }
  }

  // 更新密码
  const updatePassword = async (oldPassword: string, newPassword: string) => {
    try {
      await request.put('/user/update/password', null, { 
        params: { oldPassword, newPassword } 
      })
      ElMessage.success('密码更新成功')
    } catch (error) {
      console.error('更新密码失败:', error)
      throw error
    }
  }

  return {
    userInfo,
    token,
    login,
    logout,
    getUserInfo,
    updateProfile,
    updateEmail,
    updateUsername,
    updatePassword,
    setToken,
    clearToken,
    register
  }
}) 