<template>
  <div class="article-detail">
    <el-card class="article-card" v-loading="loading">
      <template v-if="article">
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span class="time">
            <el-icon><Clock /></el-icon>
            {{ formatDate(article.createTime) }}
          </span>
          <span class="views">
            <el-icon><View /></el-icon>
            {{ article.viewCount || 0 }} 阅读
          </span>
        </div>
        <div class="article-content markdown-body" v-html="renderedContent"></div>
      </template>
    </el-card>

    <!-- 评论区 -->
    <el-card class="comment-card">
      <CommentSection :article-id="Number(articleId)" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Clock, View } from '@element-plus/icons-vue'
import * as marked from 'marked'
import { useArticleStore } from '../../stores/article'
import CommentSection from './components/CommentSection.vue'
import 'github-markdown-css/github-markdown.css'

const route = useRoute()
const articleStore = useArticleStore()
const loading = ref(false)
const article = ref<any>(null)
const articleId = route.params.id

// 渲染Markdown内容
const renderedContent = computed(() => {
  if (!article.value?.content) return ''
  return marked.parse(article.value.content)
})

// 加载文章详情
const loadArticle = async () => {
  loading.value = true
  try {
    article.value = await articleStore.getArticleDetail(Number(articleId))
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
  } finally {
    loading.value = false
  }
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleString()
}

onMounted(() => {
  loadArticle()
})
</script>

<style scoped lang="scss">
.article-detail {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;

  .article-card {
    margin-bottom: 20px;

    .article-title {
      font-size: 28px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
      text-align: center;
    }

    .article-meta {
      display: flex;
      justify-content: center;
      gap: 24px;
      color: #666;
      margin-bottom: 24px;
      font-size: 14px;

      .time, .views {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }

    :deep(.article-content) {
      line-height: 1.8;
      color: #333;
    }
  }

  :deep(.markdown-body) {
    background-color: transparent;
    padding: 0;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
  }
}
</style> 