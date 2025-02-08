<template>
  <div class="article-detail">
    <el-card>
      <h1 class="title">{{ article?.title }}</h1>
      <div class="meta">
        <span>发布时间：{{ formatDate(article?.createTime) }}</span>
      </div>
      <div class="content markdown-body" v-html="article?.content"></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useArticleStore } from '../../stores/article'
import type { Article } from '../../types'
import { marked } from 'marked'

const route = useRoute()
const articleStore = useArticleStore()
const article = ref<Article>()

const loadArticle = async () => {
  const id = route.params.id
  if (id) {
    const data = await articleStore.getArticleDetail(Number(id))
    article.value = {
      ...data,
      content: marked(data.content)
    }
  }
}

const formatDate = (date?: string) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

onMounted(() => {
  loadArticle()
})
</script>

<style scoped lang="scss">
.article-detail {
  padding: 20px;

  .title {
    font-size: 24px;
    color: #303133;
    margin-bottom: 20px;
  }

  .meta {
    font-size: 14px;
    color: #909399;
    margin-bottom: 30px;
  }

  .content {
    line-height: 1.8;
    color: #303133;
  }
}
</style> 