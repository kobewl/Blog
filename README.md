# CodePilot 个人博客系统

一个现代化的个人博客系统，基于 Spring Boot + Vue3 开发。

## 功能特性

### 用户功能

- 用户注册/登录
- 个人信息管理
- 修改密码/邮箱
- 个人中心

### 文章功能

- 文章发布/编辑
- Markdown 编辑器
- 文章列表/详情
- 文章评论
- 文章分类/标签

### 评论功能

- 发表评论
- 评论管理
- 评论回复
- 评论审核

### 管理功能

- 用户管理
- 文章管理
- 评论管理
- 系统设置

## 技术栈

### 后端

- Spring Boot 2.7.0
- MyBatis-Plus 3.5.2
- MySQL 8.0
- Redis
- JWT

### 前端

- Vue 3
- Vite
- TypeScript
- Element Plus
- Pinia
- Vue Router
- Axios

## 开发环境要求

- JDK 1.8+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

## 快速开始

1. 克隆项目

```bash
git clone https://github.com/yourusername/codepilot-blog.git
cd codepilot-blog
```

2. 初始化数据库

```sql
create database codepilot;
```

3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

4. 启动前端

```bash
cd blog-frontend
npm install
npm run dev
```

5. 访问系统

- 前端页面: http://localhost:5173
- 后端接口: http://localhost:8080/api
- 接口文档: http://localhost:8080/api/doc.html

## 项目结构

```
codepilot-blog
├── backend                 # 后端项目
│   ├── src/main/java      # Java 源码
│   ├── src/main/resources # 配置文件
│   └── pom.xml           # Maven 配置
│
└── blog-frontend          # 前端项目
    ├── src               # 源码目录
    ├── public           # 静态资源
    └── package.json     # npm 配置
```

## 部署说明

1. 后端部署

- 修改 `application.yml` 中的数据库配置
- 打包: `mvn clean package`
- 运行: `java -jar target/blog-backend.jar`

2. 前端部署

- 修改 `.env.production` 中的接口地址
- 打包: `npm run build`
- 部署 `dist` 目录到 Nginx

## 贡献指南

1. Fork 本仓库
2. 创建特性分支
3. 提交代码
4. 创建 Pull Request

## 开源协议

本项目使用 [MIT](LICENSE) 协议
