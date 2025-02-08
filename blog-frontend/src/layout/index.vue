<template>
  <div class="layout-container">
    <div class="header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="logo">CodePilot</h1>
          <el-menu
            mode="horizontal"
            :default-active="activeMenu"
            router
            class="nav-menu"
          >
            <el-menu-item index="/">
              <el-icon><House /></el-icon>首页
            </el-menu-item>
            <el-menu-item index="/article">
              <el-icon><Document /></el-icon>文章管理
            </el-menu-item>
            <el-menu-item index="/profile">
              <el-icon><User /></el-icon>个人中心
            </el-menu-item>
          </el-menu>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" class="avatar">{{ userStore.userInfo?.username?.charAt(0) }}</el-avatar>
              <span class="username">{{ userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
    <div class="main-container">
      <router-view />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { House, Document, User, ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  min-height: 100vh;
  background-color: #f5f5f5;

  .header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 60px;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    z-index: 100;

    .header-content {
      max-width: 1200px;
      height: 100%;
      margin: 0 auto;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 20px;

      .header-left {
        display: flex;
        align-items: center;

        .logo {
          font-size: 20px;
          font-weight: bold;
          color: #409eff;
          margin-right: 40px;
        }

        .nav-menu {
          border: none;
          background: transparent;

          :deep(.el-menu-item) {
            height: 60px;
            line-height: 60px;
            
            .el-icon {
              margin-right: 4px;
            }
          }
        }
      }

      .header-right {
        .user-info {
          display: flex;
          align-items: center;
          cursor: pointer;

          .avatar {
            background: #409eff;
            color: #fff;
          }

          .username {
            margin: 0 8px;
            color: #606266;
          }
        }
      }
    }
  }

  .main-container {
    max-width: 1200px;
    margin: 80px auto 20px;
    padding: 0 20px;
  }
}
</style> 