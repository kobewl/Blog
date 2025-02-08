<template>
  <div class="comment-section">
    <!-- 评论输入框 -->
    <div class="comment-input-container">
      <el-card class="comment-input-card" :body-style="{ padding: '20px' }">
        <div class="input-header">
          <div class="user-info" v-if="userStore.userInfo">
            <el-avatar :size="40" class="user-avatar">
              {{ userStore.userInfo.username.charAt(0).toUpperCase() }}
            </el-avatar>
            <div class="user-meta">
              <span class="username">{{ userStore.userInfo.username }}</span>
              <el-tag size="small" :type="userStore.userInfo.role === 1 ? 'danger' : 'info'" effect="dark">
                {{ userStore.userInfo.role === 1 ? '管理员' : '用户' }}
              </el-tag>
            </div>
          </div>
          <div class="login-prompt" v-else>
            <el-alert
              title="请先登录后发表评论"
              type="info"
              :closable="false"
              effect="dark"
              class="login-alert"
            />
          </div>
        </div>
        
        <div class="input-area">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            :placeholder="replyTo ? `回复 @${replyTo.username}` : '写下你的评论...'"
            :maxlength="500"
            show-word-limit
            resize="none"
          />
          <div class="input-actions">
            <el-button v-if="replyTo" link @click="cancelReply">
              <el-icon><Close /></el-icon>
              取消回复
            </el-button>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">
              <el-icon><Edit /></el-icon>
              {{ replyTo ? '回复' : '发表评论' }}
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list" v-loading="loading">
      <div class="comment-header">
        <el-space>
          <el-icon><ChatLineRound /></el-icon>
          <h3>全部评论 <el-badge :value="totalComments" type="primary" /></h3>
        </el-space>
        <el-radio-group v-model="sortOrder" size="small">
          <el-radio-button label="newest">最新</el-radio-button>
          <el-radio-button label="oldest">最早</el-radio-button>
        </el-radio-group>
      </div>
      
      <div v-if="comments.length === 0" class="no-comment">
        <el-empty description="暂无评论，快来抢沙发吧！">
          <template #image>
            <el-icon :size="60"><ChatDotRound /></el-icon>
          </template>
        </el-empty>
      </div>
      
      <div v-else class="comment-items">
        <transition-group name="comment-list">
          <div v-for="comment in groupedComments" :key="comment.id" class="comment-thread">
            <!-- 主评论 -->
            <el-card class="comment-card" shadow="hover">
              <div class="comment-user">
                <div class="user-avatar">
                  <el-avatar :size="40">{{ comment.username?.charAt(0)?.toUpperCase() }}</el-avatar>
                  <div class="user-info">
                    <span class="username">{{ comment.username }}</span>
                    <el-tag size="small" effect="dark" class="user-role">
                      {{ comment.role === 1 ? '管理员' : '用户' }}
                    </el-tag>
                  </div>
                </div>
                <div class="comment-meta">
                  <el-tooltip :content="formatFullDate(comment.createTime)" placement="top">
                    <span class="time">{{ formatDate(comment.createTime) }}</span>
                  </el-tooltip>
                </div>
              </div>
              <div class="comment-content">
                {{ comment.content }}
              </div>
              <div class="comment-actions">
                <el-button 
                  type="primary" 
                  link 
                  @click="handleReply(comment)"
                  v-if="userStore.userInfo"
                >
                  <el-icon><ChatDotSquare /></el-icon>
                  回复
                </el-button>
                <el-button 
                  type="danger" 
                  link 
                  @click="handleDelete(comment.id)"
                  :loading="deleting === comment.id"
                  v-if="canDelete(comment)"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </el-card>
            
            <!-- 回复评论 -->
            <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
              <transition-group name="reply-list">
                <el-card 
                  v-for="reply in comment.replies" 
                  :key="reply.id" 
                  class="reply-card" 
                  shadow="hover"
                >
                  <div class="comment-user">
                    <div class="user-avatar">
                      <el-avatar :size="32">{{ reply.username?.charAt(0)?.toUpperCase() }}</el-avatar>
                      <div class="user-info">
                        <span class="username">{{ reply.username }}</span>
                        <el-tag size="small" effect="dark" class="user-role">
                          {{ reply.role === 1 ? '管理员' : '用户' }}
                        </el-tag>
                      </div>
                    </div>
                    <div class="comment-meta">
                      <span class="reply-to">回复</span>
                      <span class="reply-username">@{{ reply.toUsername }}</span>
                      <el-tooltip :content="formatFullDate(reply.createTime)" placement="top">
                        <span class="time">{{ formatDate(reply.createTime) }}</span>
                      </el-tooltip>
                    </div>
                  </div>
                  <div class="comment-content">
                    {{ reply.content }}
                  </div>
                  <div class="comment-actions">
                    <el-button 
                      type="primary" 
                      link 
                      @click="handleReply(reply)"
                      v-if="userStore.userInfo"
                    >
                      <el-icon><ChatDotSquare /></el-icon>
                      回复
                    </el-button>
                    <el-button 
                      type="danger" 
                      link 
                      @click="handleDelete(reply.id)"
                      :loading="deleting === reply.id"
                      v-if="canDelete(reply)"
                    >
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-button>
                  </div>
                </el-card>
              </transition-group>
            </div>
          </div>
        </transition-group>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          background
          @size-change="val => { pageSize = val; fetchComments(); }"
          @current-change="val => { currentPage = val; fetchComments(); }"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'CommentSection'
})
</script>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Edit, 
  Delete, 
  ChatLineRound, 
  ChatDotRound, 
  ChatDotSquare,
  Close 
} from '@element-plus/icons-vue'
import { useUserStore } from '../../../stores/user'
import request from '../../../utils/request'
import type { Comment, PageResult } from '../../../types'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const props = defineProps<{
  articleId: number
}>()

const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const deleting = ref<number | null>(null)
const commentContent = ref('')
const comments = ref<Comment[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const replyTo = ref<Comment | null>(null)
const sortOrder = ref('newest')

// 获取评论列表
const fetchComments = async () => {
  loading.value = true
  try {
    const response = await request.get<PageResult<Comment>>(`/comment/article/${props.articleId}`, {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    comments.value = response.records || []
    total.value = response.total || 0
  } catch (error) {
    console.error('获取评论失败:', error)
    ElMessage.error('获取评论失败')
    comments.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 设置回复目标
const handleReply = (comment: Comment) => {
  replyTo.value = comment
  // 滚动到评论框
  const commentInput = document.querySelector('.comment-input')
  commentInput?.scrollIntoView({ behavior: 'smooth' })
}

// 取消回复
const cancelReply = () => {
  replyTo.value = null
  commentContent.value = ''
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
      content: commentContent.value.trim(),
      articleId: props.articleId,
      toUserId: replyTo.value?.userId,
      parentId: replyTo.value?.id
    })
    
    // 清空输入框并重新加载评论
    commentContent.value = ''
    replyTo.value = null
    await fetchComments()
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
    await fetchComments()
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
const canDelete = (comment: Comment) => {
  const currentUser = userStore.userInfo
  return currentUser && (
    comment.userId === currentUser.id || // 评论作者
    currentUser.role === 1 // 管理员
  )
}

const formatDate = (date: string) => {
  return dayjs(date).fromNow()
}

const formatFullDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

// 根据排序方式对评论进行排序
const sortedComments = computed(() => {
  return [...comments.value].sort((a, b) => {
    const timeA = new Date(a.createTime).getTime()
    const timeB = new Date(b.createTime).getTime()
    return sortOrder.value === 'newest' ? timeB - timeA : timeA - timeB
  })
})

// 修改 groupedComments computed 属性，使用 sortedComments
const groupedComments = computed(() => {
  const result: (Comment & { replies?: Comment[] })[] = []
  const mainComments = sortedComments.value.filter(comment => !comment.parentId)
  const replies = sortedComments.value.filter(comment => comment.parentId)

  // 将回复按父评论ID分组
  const replyMap = new Map<number, Comment[]>()
  replies.forEach(reply => {
    if (reply.parentId) {
      const commentReplies = replyMap.get(reply.parentId) || []
      commentReplies.push(reply)
      replyMap.set(reply.parentId, commentReplies)
    }
  })

  // 构建主评论及其回复
  mainComments.forEach(comment => {
    const commentWithReplies = {
      ...comment,
      replies: replyMap.get(comment.id) || []
    }
    result.push(commentWithReplies)
  })

  return result
})

// 计算总评论数（包括回复）
const totalComments = computed(() => {
  return comments.value.length
})

onMounted(() => {
  fetchComments()
})
</script>

<style scoped lang="scss">
.comment-section {
  .comment-input-container {
    margin-bottom: 32px;

    .comment-input-card {
      background: #fff;
      border: none;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
      }

      .input-header {
        margin-bottom: 16px;

        .user-info {
          display: flex;
          align-items: center;
          gap: 12px;

          .user-meta {
            display: flex;
            flex-direction: column;
            gap: 4px;

            .username {
              font-weight: 600;
              color: #333;
            }
          }
        }

        .login-alert {
          margin: 0;
        }
      }

      .input-area {
        .el-input {
          margin-bottom: 12px;
          
          :deep(.el-textarea__inner) {
            border-radius: 8px;
            padding: 12px;
            font-size: 14px;
            
            &:focus {
              box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
            }
          }
        }
      }
    }
  }

  .comment-list {
    .comment-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      padding-bottom: 16px;
      border-bottom: 1px solid #eee;

      h3 {
        display: flex;
        align-items: center;
        gap: 8px;
        margin: 0;
        font-size: 18px;
        color: #333;
      }
    }

    .comment-thread {
      margin-bottom: 24px;
    }

    .comment-card, .reply-card {
      background: #fff;
      border: none;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
      }

      .comment-user {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .user-avatar {
          display: flex;
          align-items: center;
          gap: 12px;

          .user-info {
            display: flex;
            align-items: center;
            gap: 8px;

            .username {
              font-weight: 600;
              color: #333;
            }

            .user-role {
              font-size: 12px;
            }
          }
        }

        .comment-meta {
          display: flex;
          align-items: center;
          gap: 8px;
          color: #909399;
          font-size: 13px;

          .reply-username {
            color: #409eff;
            font-weight: 500;
          }
        }
      }

      .comment-content {
        margin: 12px 0;
        line-height: 1.6;
        color: #333;
        font-size: 14px;
      }

      .comment-actions {
        display: flex;
        justify-content: flex-end;
        gap: 16px;
        margin-top: 12px;

        .el-button {
          padding: 4px 8px;
          
          .el-icon {
            margin-right: 4px;
          }
        }
      }
    }

    .reply-list {
      margin-left: 48px;
      margin-top: 16px;

      .reply-card {
        margin-bottom: 12px;
        background: #f8f9fa;
      }
    }
  }

  .pagination {
    margin-top: 32px;
    display: flex;
    justify-content: flex-end;
  }
}

// 动画效果
.comment-list-enter-active,
.comment-list-leave-active,
.reply-list-enter-active,
.reply-list-leave-active {
  transition: all 0.3s ease;
}

.comment-list-enter-from,
.comment-list-leave-to,
.reply-list-enter-from,
.reply-list-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.comment-list-move,
.reply-list-move {
  transition: transform 0.3s ease;
}
</style> 