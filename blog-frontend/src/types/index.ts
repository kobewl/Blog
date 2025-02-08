// 定义通用接口
export interface User {
  id: number;
  username: string;
  email: string;
  role: number;
  status: number;
  createTime: string;
  updateTime: string;
}

export interface Article {
  id: number;
  title: string;
  content: string;
  userId: number;
  status: number;
  createTime: string;
  updateTime: string;
}

export interface Comment {
  id: number;
  content: string;
  userId: number;
  articleId: number;
  status: number;
  createTime: string;
  updateTime: string;
} 