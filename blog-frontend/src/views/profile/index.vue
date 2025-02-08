<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <!-- 用户信息卡片 -->
        <el-card class="info-card" shadow="hover">
          <div class="user-info">
            <el-avatar :size="80" class="avatar">{{ userStore.userInfo?.username?.charAt(0)?.toUpperCase() }}</el-avatar>
            <h2>{{ userStore.userInfo?.username }}</h2>
            <p class="email">{{ userStore.userInfo?.email }}</p>
            <div class="role-tag">
              <el-tag :type="userStore.userInfo?.role === 1 ? 'danger' : 'info'">
                {{ userStore.userInfo?.role === 1 ? '管理员' : '普通用户' }}
              </el-tag>
            </div>
            <div class="join-time">
              加入时间：{{ formatDate(userStore.userInfo?.createTime) }}
            </div>
            <el-button type="primary" class="edit-btn" @click="handleEdit">
              编辑资料
            </el-button>
          </div>
        </el-card>

        <!-- 数据统计卡片 -->
        <el-card class="stats-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>数据统计</span>
            </div>
          </template>
          <div class="stats-list">
            <div class="stats-item">
              <div class="label">文章数</div>
              <div class="value">{{ stats.articleCount }}</div>
            </div>
            <div class="stats-item">
              <div class="label">评论数</div>
              <div class="value">{{ stats.commentCount }}</div>
            </div>
            <div class="stats-item">
              <div class="label">访问量</div>
              <div class="value">{{ stats.viewCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <!-- 最近动态卡片 -->
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近动态</span>
            </div>
          </template>
          <div class="timeline">
            <el-empty v-if="activities.length === 0" description="暂无动态" />
            <el-timeline v-else>
              <el-timeline-item
                v-for="(activity, index) in activities"
                :key="index"
                :timestamp="activity.time"
                :type="activity.type"
              >
                {{ activity.content }}
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑个人信息对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑个人信息"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="form.oldPassword" type="password" show-password placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref<FormInstance>()

// 统计数据
const stats = ref({
  articleCount: 0,
  commentCount: 0,
  viewCount: 0
})

// 最近活动
const activities = ref([])

const form = ref({
  username: '',
  email: '',
  oldPassword: '',
  newPassword: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  oldPassword: [
    { required: false, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: false, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

// 打开编辑对话框
const handleEdit = () => {
  form.value = {
    username: userStore.userInfo?.username || '',
    email: userStore.userInfo?.email || '',
    oldPassword: '',
    newPassword: ''
  }
  dialogVisible.value = true
}

// 提交编辑表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 更新用户名
        if (form.value.username !== userStore.userInfo?.username) {
          await userStore.updateUsername(form.value.username)
        }
        
        // 更新邮箱
        if (form.value.email !== userStore.userInfo?.email) {
          await userStore.updateEmail(form.value.email)
        }
        
        // 更新密码
        if (form.value.oldPassword && form.value.newPassword) {
          await userStore.updatePassword(form.value.oldPassword, form.value.newPassword)
        }
        
        dialogVisible.value = false
      } catch (error: any) {
        ElMessage.error(error?.message || '更新失败，请重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const formatDate = (date?: string) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

// 获取用户统计数据
const loadUserStats = async () => {
  // TODO: 从后端获取用户统计数据
  // 这里暂时使用模拟数据
  stats.value = {
    articleCount: 5,
    commentCount: 10,
    viewCount: 100
  }
}

// 获取用户最近活动
const loadUserActivities = async () => {
  // TODO: 从后端获取用户最近活动
  // 这里暂时使用空数组，等待后端接口实现
  activities.value = []
}

onMounted(async () => {
  await Promise.all([
    loadUserStats(),
    loadUserActivities()
  ])
})
</script>

<style scoped lang="scss">
.profile-container {
  padding: 20px;
  
  .info-card {
    margin-bottom: 20px;

    .user-info {
      text-align: center;
      padding: 20px 0;

      .avatar {
        background: #409eff;
        color: #fff;
        font-size: 32px;
        margin-bottom: 16px;
      }

      h2 {
        font-size: 20px;
        color: #303133;
        margin-bottom: 8px;
      }

      .email {
        color: #909399;
        font-size: 14px;
        margin-bottom: 16px;
      }

      .role-tag {
        margin-bottom: 16px;
      }

      .join-time {
        color: #909399;
        font-size: 13px;
        margin-bottom: 24px;
      }

      .edit-btn {
        width: 120px;
      }
    }
  }

  .stats-card {
    .card-header {
      font-weight: bold;
    }

    .stats-list {
      display: flex;
      justify-content: space-around;
      text-align: center;
      padding: 20px 0;

      .stats-item {
        .label {
          color: #909399;
          font-size: 13px;
          margin-bottom: 8px;
        }

        .value {
          font-size: 24px;
          color: #303133;
          font-weight: bold;
        }
      }
    }
  }

  .timeline {
    padding: 20px;
    min-height: 200px;

    :deep(.el-timeline-item__content) {
      color: #303133;
      font-size: 14px;
    }

    :deep(.el-timeline-item__timestamp) {
      color: #909399;
      font-size: 13px;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 