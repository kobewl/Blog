<template>
  <div class="comment-section">
    <!-- 评论输入框 -->
    <div class="comment-input">
      <el-input
        v-model="commentContent"
        type="textarea"
        :rows="3"
        placeholder="写下你的评论..."
        maxlength="500"
        show-word-limit
      />
      <el-button 
        type="primary" 
        @click="submitComment" 
        :loading="submitting"
        class="submit-btn"
      >
        发表评论
      </el-button>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <div class="comment-header">
        <span class="comment-count">全部评论 ({{ total }})</span>
      </div>
      
      <div v-if="comments.length === 0" class="no-comment">
        暂无评论，快来抢沙发吧！
      </div>

      <div v-else class="comment-items">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-user">
            <el-avatar :size="40">{{ comment.username?.charAt(0) }}</el-avatar>
            <div class="user-info">
              <span class="username">{{ comment.username }}</span>
              <span class="time">{{ formatTime(comment.createTime) }}</span>
            </div>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
          <div class="comment-actions">
            <el-button 
              v-if="canDelete(comment)" 
              type="danger" 
              link 
              @click="deleteComment(comment.id)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../stores/user'
import request from '../utils/request'
import { formatTime } from '../utils/format'

const props = defineProps<{
  articleId: number
}>()

const userStore = useUserStore()
const comments = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const commentContent = ref('')
const submitting = ref(false)

// 获取评论列表
const fetchComments = async () => {
  try {
    const { data } = await request.get(`/comment/article/${props.articleId}`, {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    comments.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('获取评论失败:', error)
    ElMessage.error('获取评论失败')
  }
}

// 提交评论
const submitComment = async () => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    return
  }

  if (!commentContent.value.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }

  submitting.value = true
  try {
    await request.post('/comment/create', {
      articleId: props.articleId,
      content: commentContent.value.trim()
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    fetchComments()
  } catch (error) {
    console.error('发表评论失败:', error)
    ElMessage.error('发表评论失败')
  } finally {
    submitting.value = false
  }
}

// 删除评论
const deleteComment = async (commentId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      type: 'warning'
    })
    
    await request.delete(`/comment/${commentId}`)
    ElMessage.success('删除成功')
    fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除评论失败')
    }
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
  fetchComments()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchComments()
}

onMounted(() => {
  fetchComments()
})
</script>

<style scoped lang="scss">
.comment-section {
  margin-top: 20px;
  
  .comment-input {
    margin-bottom: 20px;
    
    .submit-btn {
      margin-top: 10px;
      float: right;
    }
  }
  
  .comment-list {
    .comment-header {
      margin-bottom: 20px;
      padding-bottom: 10px;
      border-bottom: 1px solid #eee;
      
      .comment-count {
        font-size: 16px;
        font-weight: 500;
      }
    }
    
    .no-comment {
      text-align: center;
      color: #909399;
      padding: 20px 0;
    }
    
    .comment-items {
      .comment-item {
        padding: 20px 0;
        border-bottom: 1px solid #eee;
        
        .comment-user {
          display: flex;
          align-items: center;
          margin-bottom: 10px;
          
          .user-info {
            margin-left: 10px;
            
            .username {
              font-weight: 500;
              margin-right: 10px;
            }
            
            .time {
              color: #909399;
              font-size: 12px;
            }
          }
        }
        
        .comment-content {
          margin: 10px 0;
          line-height: 1.6;
        }
        
        .comment-actions {
          display: flex;
          justify-content: flex-end;
        }
      }
    }
    
    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  }
}
</style> 