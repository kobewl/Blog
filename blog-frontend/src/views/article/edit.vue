<template>
  <div class="article-edit">
    <div class="edit-header">
      <el-input
        v-model="articleForm.title"
        placeholder="请输入文章标题"
        class="title-input"
      />
      <div class="header-actions">
        <el-button @click="handleSave(0)">保存草稿</el-button>
        <el-button type="primary" @click="handleSave(1)">发布文章</el-button>
      </div>
    </div>

    <md-editor
      v-model="articleForm.content"
      style="height: calc(100vh - 200px)"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { useArticleStore } from '../../stores/article'
import { useUserStore } from '../../stores/user'

const route = useRoute()
const router = useRouter()
const articleStore = useArticleStore()
const userStore = useUserStore()

const articleForm = ref({
  id: undefined,
  title: '',
  content: '',
  status: 0
})

const handleSave = async (status: number) => {
  if (!articleForm.value.title) {
    ElMessage.warning('请输入文章标题')
    return
  }
  if (!articleForm.value.content) {
    ElMessage.warning('请输入文章内容')
    return
  }

  try {
    // 确保用户已登录
    if (!userStore.userInfo?.id) {
      ElMessage.error('用户未登录')
      router.push('/login')
      return
    }

    // 设置文章数据
    const articleData = {
      ...articleForm.value,
      status,
      userId: userStore.userInfo.id
    }

    console.log('保存文章数据:', articleData)

    // 如果是编辑已有文章
    if (articleForm.value.id) {
      await articleStore.updateArticle(articleData)
    } else {
      // 如果是新建文章
      await articleStore.createArticle(articleData)
    }

    // 保存成功后再跳转
    router.push('/article')
  } catch (error: any) {
    console.error('保存文章失败:', error)
    // 根据错误类型显示不同的错误信息
    if (error?.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      userStore.clearToken()
      router.push('/login')
    } else {
      ElMessage.error(error?.response?.data?.message || '保存失败，请重试')
    }
  }
}

onMounted(async () => {
  try {
    // 确保用户已登录
    if (!userStore.userInfo) {
      try {
        await userStore.getUserInfo()
      } catch (error) {
        console.error('获取用户信息失败:', error)
        ElMessage.error('请先登录')
        router.push('/login')
        return
      }
    }
    
    // 如果有文章ID，获取文章详情
    const id = route.params.id
    if (id) {
      try {
        const article = await articleStore.getArticleDetail(Number(id))
        articleForm.value = article
      } catch (error) {
        console.error('获取文章详情失败:', error)
        ElMessage.error('获取文章详情失败')
        router.push('/article')
      }
    }
  } catch (error) {
    console.error('初始化失败:', error)
    ElMessage.error('加载失败，请重试')
  }
})
</script>

<style scoped lang="scss">
.article-edit {
  background: #fff;
  padding: 20px;
  border-radius: 4px;

  .edit-header {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;

    .title-input {
      flex: 1;
    }

    .header-actions {
      display: flex;
      gap: 10px;
    }
  }
}
</style> 