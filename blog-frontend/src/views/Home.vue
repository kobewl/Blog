<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="18">
        <el-card class="article-list" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="title">最新文章</span>
            </div>
          </template>
          <div v-loading="loading">
            <div v-for="article in articleList" :key="article.id" class="article-item">
              <h3 class="title" @click="handleArticleClick(article)">{{ article.title }}</h3>
              <div class="info">
                <span class="time">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(article.createTime) }}
                </span>
                <span class="status">
                  <el-tag :type="article.status === 1 ? 'success' : 'info'" size="small">
                    {{ article.status === 1 ? '已发布' : '草稿' }}
                  </el-tag>
                </span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="user-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="title">个人信息</span>
            </div>
          </template>
          <div class="user-info">
            <el-avatar :size="64" class="avatar">{{ userStore.userInfo?.username?.charAt(0) }}</el-avatar>
            <h3>{{ userStore.userInfo?.username }}</h3>
            <p>{{ userStore.userInfo?.email }}</p>
            <div class="stats">
              <div class="stat-item">
                <div class="value">{{ articleList.length }}</div>
                <div class="label">文章</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useArticleStore } from '../stores/article'
import type { Article } from '../types'
import { Clock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const articleStore = useArticleStore()

const loading = ref(false)
const articleList = ref<Article[]>([])

const loadArticles = async () => {
  loading.value = true
  try {
    const res = await articleStore.getArticleList({
      page: 1,
      size: 10
    })
    articleList.value = res.records
  } finally {
    loading.value = false
  }
}

const handleArticleClick = (article: Article) => {
  router.push(`/article/${article.id}`)
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleString()
}

onMounted(() => {
  loadArticles()
})
</script>

<style scoped lang="scss">
.home-container {
  .article-list {
    .card-header {
      .title {
        font-size: 18px;
        font-weight: bold;
        color: #303133;
      }
    }

    .article-item {
      padding: 20px 0;
      border-bottom: 1px solid #ebeef5;
      cursor: pointer;

      &:last-child {
        border-bottom: none;
      }

      .title {
        font-size: 16px;
        color: #303133;
        margin-bottom: 12px;
        font-weight: 500;

        &:hover {
          color: #409eff;
        }
      }

      .info {
        display: flex;
        align-items: center;
        gap: 16px;
        font-size: 13px;
        color: #909399;

        .time {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }

  .user-card {
    .card-header {
      .title {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
      }
    }

    .user-info {
      text-align: center;
      
      .avatar {
        margin-bottom: 16px;
        background: #409eff;
        color: #fff;
        font-size: 24px;
      }

      h3 {
        margin-bottom: 8px;
        color: #303133;
        font-size: 16px;
      }

      p {
        color: #909399;
        font-size: 14px;
        margin-bottom: 16px;
      }

      .stats {
        display: flex;
        justify-content: center;
        border-top: 1px solid #ebeef5;
        padding-top: 16px;

        .stat-item {
          padding: 0 16px;

          .value {
            font-size: 20px;
            color: #303133;
            font-weight: bold;
          }

          .label {
            font-size: 12px;
            color: #909399;
            margin-top: 4px;
          }
        }
      }
    }
  }
}
</style> 