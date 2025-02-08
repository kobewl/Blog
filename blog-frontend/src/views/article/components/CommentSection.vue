<template>
  <div class="comment-section">
    <!-- 评论输入框 -->
    <div class="comment-input">
      <el-input
        v-model="commentContent"
        type="textarea"
        :rows="3"
        placeholder="写下你的评论..."
        :maxlength="500"
        show-word-limit
      />
      <div class="input-actions">
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          发表评论
        </el-button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list" v-loading="loading">
      <div class="comment-header">
        <h3>全部评论 ({{ total }})</h3>
      </div>
      
      <div v-if="comments.length === 0" class="no-comment">
        暂无评论，快来抢沙发吧！
      </div>
      
      <div v-else class="comment-items">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-user">
            <el-avatar :size="40">{{ comment.username?.charAt(0)?.toUpperCase() }}</el-avatar>
            <div class="user-info">
              <span class="username">{{ comment.username }}</span>
              <span class="time">{{ formatDate(comment.createTime) }}</span>
            </div>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
          <div class="comment-actions" v-if="canDelete(comment)">
            <el-button 
              type="danger" 
              link 
              @click="handleDelete(comment.id)"
              :loading="deleting === comment.id"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../../stores/user'
import request from '../../../utils/request'

const props = defineProps<{
  articleId: number
}>()

const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const deleting = ref<number | null>(null)
const commentContent = ref('')
const comments = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 加载评论列表
const loadComments = async () => {
  loading.value = true
  try {
    const res = await request.get(`/comment/article/${props.articleId}`, {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    comments.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取评论列表失败:', error)
    ElMessage.error('获取评论失败')
  } finally {
    loading.value = false
  }
}

// 提交评论
const handleSubmit = async () => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    return
  }

  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  submitting.value = true
  try {
    await request.post('/comment/create', {
      content: commentContent.value,
      articleId: props.articleId
    })
    
    // 清空输入框并重新加载评论
    commentContent.value = ''
    await loadComments()
    ElMessage.success('评论成功')
  } catch (error) {
    console.error('提交评论失败:', error)
    ElMessage.error('评论失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 删除评论
const handleDelete = async (commentId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })

    deleting.value = commentId
    await request.delete(`/comment/${commentId}`)
    await loadComments()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  } finally {
    deleting.value = null
  }
}

// 判断是否可以删除评论
const canDelete = (comment: any) => {
  const currentUser = userStore.userInfo
  return currentUser && (
    comment.userId === currentUser.id || // 评论作者
    currentUser.role === 1 // 管理员
  )
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadComments()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadComments()
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleString()
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped lang="scss">
.comment-section {
  .comment-input {
    margin-bottom: 24px;

    .input-actions {
      margin-top: 16px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .comment-list {
    .comment-header {
      margin-bottom: 20px;
      
      h3 {
        font-size: 18px;
        font-weight: 600;
        color: #333;
      }
    }

    .no-comment {
      text-align: center;
      color: #909399;
      padding: 40px 0;
    }

    .comment-items {
      .comment-item {
        padding: 20px 0;
        border-bottom: 1px solid #ebeef5;

        &:last-child {
          border-bottom: none;
        }

        .comment-user {
          display: flex;
          align-items: center;
          margin-bottom: 12px;

          .user-info {
            margin-left: 12px;

            .username {
              font-weight: 500;
              color: #333;
              margin-right: 12px;
            }

            .time {
              color: #909399;
              font-size: 13px;
            }
          }
        }

        .comment-content {
          color: #606266;
          line-height: 1.6;
          margin-left: 52px;
        }

        .comment-actions {
          margin-top: 8px;
          margin-left: 52px;
          display: flex;
          justify-content: flex-end;
        }
      }
    }

    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style> 