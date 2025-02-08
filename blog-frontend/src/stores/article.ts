import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Article } from '../types'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

export const useArticleStore = defineStore('article', () => {
  const articleList = ref<Article[]>([])
  const total = ref(0)
  const loading = ref(false)

  // 获取文章列表
  const getArticleList = async (params: {
    page: number
    size: number
    keyword?: string
  }) => {
    loading.value = true
    try {
      const res = await request.get('/article/list', { params })
      articleList.value = res.records || []
      total.value = res.total || 0
      return res
    } catch (error) {
      console.error('获取文章列表失败:', error)
      articleList.value = []
      total.value = 0
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取用户的文章列表
  const getUserArticles = async (params: {
    page: number
    size: number
  }) => {
    loading.value = true
    try {
      const res = await request.get('/article/user/list', { params })
      articleList.value = res.records || []
      total.value = res.total || 0
      return res
    } catch (error) {
      console.error('获取用户文章列表失败:', error)
      articleList.value = []
      total.value = 0
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取文章详情
  const getArticleDetail = async (id: number): Promise<Article> => {
    try {
      const res = await request.get(`/article/${id}`)
      return res
    } catch (error) {
      console.error('获取文章详情失败:', error)
      throw error
    }
  }

  // 创建文章
  const createArticle = async (article: Partial<Article>) => {
    try {
      // 确保状态字段存在
      if (article.status === undefined) {
        article.status = 0 // 默认为草稿状态
      }
      const res = await request.post('/article/create', article)
      ElMessage.success(article.status === 1 ? '文章发布成功' : '草稿保存成功')
      return res
    } catch (error) {
      console.error('创建文章失败:', error)
      throw error
    }
  }

  // 更新文章
  const updateArticle = async (article: Partial<Article>) => {
    try {
      const res = await request.put('/article/update', article)
      ElMessage.success(article.status === 1 ? '文章发布成功' : '草稿保存成功')
      return res
    } catch (error) {
      console.error('更新文章失败:', error)
      throw error
    }
  }

  // 删除文章
  const deleteArticle = async (id: number) => {
    try {
      await request.delete(`/article/${id}`)
      ElMessage.success('删除成功')
    } catch (error) {
      console.error('删除文章失败:', error)
      throw error
    }
  }

  return {
    articleList,
    total,
    loading,
    getArticleList,
    getUserArticles,
    getArticleDetail,
    createArticle,
    updateArticle,
    deleteArticle
  }
}) 