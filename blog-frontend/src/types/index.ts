// 通用分页结果接口
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

// 评论接口
export interface Comment {
  id: number
  content: string
  userId: number
  username: string
  createTime: string
  toUserId?: number
  toUsername?: string
  articleId: number
  parentId?: number
}

// 用户接口
export interface User {
  id: number
  username: string
  email: string
  role: number
  createTime: string
  status: number
}

// 文章接口
export interface Article {
  id: number
  title: string
  content: string
  userId: number
  username?: string
  status: number
  viewCount: number
  createTime: string
  updateTime: string
}

// API 响应接口
export interface ApiResponse<T> {
  code: number
  data: T
  message: string
} 