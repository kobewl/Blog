<template>
  <div class="login-container">
    <div class="login-box">
      <h2>个人博客系统</h2>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="登录" name="login">
            <el-form-item prop="username">
              <el-input 
                v-model="form.username"
                placeholder="用户名"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
          </el-tab-pane>
          <el-tab-pane label="注册" name="register">
            <el-form-item prop="username">
              <el-input 
                v-model="form.username"
                placeholder="用户名"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item prop="email" v-if="activeTab === 'register'">
              <el-input
                v-model="form.email"
                placeholder="邮箱"
                prefix-icon="Message"
              />
            </el-form-item>
          </el-tab-pane>
        </el-tabs>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleSubmit" 
            :loading="loading" 
            class="submit-btn"
          >
            {{ activeTab === 'login' ? '登录' : '注册' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const activeTab = ref('login')

const form = reactive({
  username: '',
  password: '',
  email: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (activeTab.value === 'login') {
          await userStore.login(form.username, form.password)
          
          // 先获取用户信息
          await userStore.getUserInfo()
          
          ElMessage.success('登录成功')
          // 等待路由跳转完成
          await router.push('/')
        } else {
          await userStore.register(form)
          ElMessage.success('注册成功，请登录')
          activeTab.value = 'login'
          form.password = ''
        }
      } catch (error) {
        console.error('操作失败')
        ElMessage.error('登录失败，请检查用户名和密码')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f5f5;

  .login-box {
    width: 400px;
    padding: 40px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    h2 {
      text-align: center;
      margin-bottom: 30px;
      color: #409eff;
    }

    .submit-btn {
      width: 100%;
    }
  }
}
</style> 