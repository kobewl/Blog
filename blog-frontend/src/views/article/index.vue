<template>
  <div class="article-container">
    <div class="article-header">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索文章标题"
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <el-button type="primary" class="create-btn" @click="handleCreate">
        <el-icon><Plus /></el-icon>写文章
      </el-button>
    </div>

    <div class="article-list">
      <el-table 
        :data="articleStore.articleList" 
        v-loading="loading" 
        style="width: 100%"
        :header-cell-style="{
          background: 'var(--el-bg-color-page)',
          color: 'var(--el-text-color-primary)',
          fontWeight: '600'
        }"
      >
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="article-title">
              <span class="title-text">{{ row.title }}</span>
              <span class="view-count" v-if="row.viewCount">
                <el-icon><View /></el-icon>
                {{ row.viewCount }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ formatDate(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag 
              :type="row.status === 1 ? 'success' : 'info'"
              class="status-tag"
              effect="light"
            >
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button 
                link 
                type="primary" 
                class="action-btn"
                @click="handleEdit(row)"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button 
                link 
                type="danger" 
                class="action-btn"
                @click="handleDelete(row)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="articleStore.total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next"
        @update:current-page="handleCurrentChange"
        @update:page-size="handleSizeChange"
        background
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Search, Plus, Edit, Delete, View, Clock } from '@element-plus/icons-vue'
import { useArticleStore } from '../../stores/article'

const router = useRouter()
const articleStore = useArticleStore()

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

const loadArticles = async () => {
  loading.value = true
  try {
    await articleStore.getUserArticles({
      page: currentPage.value,
      size: pageSize.value
    })
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadArticles()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadArticles()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadArticles()
}

const handleCreate = () => {
  router.push('/article/edit')
}

const handleEdit = (row: any) => {
  router.push(`/article/edit/${row.id}`)
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', {
    type: 'warning',
    confirmButtonClass: 'el-button--danger',
    cancelButtonText: '取消',
    confirmButtonText: '确定删除'
  }).then(async () => {
    await articleStore.deleteArticle(row.id)
    ElMessage.success('删除成功')
    loadArticles()
  })
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleString()
}

onMounted(() => {
  loadArticles()
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.article-container {
  background: $bg-primary;
  padding: $container-padding;
  border-radius: $border-radius-lg;
  box-shadow: $shadow-sm;
  min-height: calc(100vh - 120px);

  .article-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    gap: 16px;

    .search-box {
      flex: 1;
      max-width: 400px;

      .search-input {
        :deep(.el-input__wrapper) {
          box-shadow: $shadow-sm;
          transition: $transition-fast;

          &:hover, &:focus {
            box-shadow: $shadow-md;
          }
        }
      }
    }

    .create-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 24px;
      font-weight: 500;
      transition: $transition-fast;

      &:hover {
        transform: translateY(-1px);
        box-shadow: $shadow-md;
      }
    }
  }

  .article-list {
    margin-bottom: 24px;

    :deep(.el-table) {
      border-radius: $border-radius-md;
      overflow: hidden;
      box-shadow: $shadow-sm;

      .el-table__header-wrapper {
        th {
          font-weight: 600;
          color: $text-primary;
          background: $bg-secondary;
        }
      }

      .el-table__row {
        transition: $transition-fast;

        &:hover {
          background-color: rgba($primary-color, 0.05);
        }
      }
    }

    .article-title {
      display: flex;
      align-items: center;
      gap: 12px;

      .title-text {
        color: $text-primary;
        font-weight: 500;
      }

      .view-count {
        display: flex;
        align-items: center;
        gap: 4px;
        color: $text-light;
        font-size: 0.9em;
      }
    }

    .time-info {
      display: flex;
      align-items: center;
      gap: 6px;
      color: $text-secondary;
    }

    .status-tag {
      font-weight: 500;
      padding: 4px 12px;
      border-radius: $border-radius-sm;
    }

    .action-buttons {
      display: flex;
      gap: 16px;

      .action-btn {
        display: flex;
        align-items: center;
        gap: 4px;
        padding: 4px 8px;
        border-radius: $border-radius-sm;
        transition: $transition-fast;

        &:hover {
          background: rgba($primary-color, 0.1);
        }

        &.el-button--danger:hover {
          background: rgba(#f43f5e, 0.1);
        }
      }
    }
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    padding-top: 16px;
    border-top: 1px solid $border-light;

    :deep(.el-pagination) {
      --el-pagination-hover-color: #{$primary-color};

      .el-pagination__total {
        margin-right: 16px;
      }

      .el-pagination__sizes {
        margin-right: 16px;
      }

      button {
        &:not(:disabled):hover {
          color: $primary-color;
        }
      }

      .el-pager li {
        &:not(.is-disabled):hover {
          color: $primary-color;
        }
        &.is-active {
          background-color: $primary-color;
        }
      }
    }
  }
}
</style> 