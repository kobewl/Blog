package com.codepilot.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codepilot.blog.entity.Article;

public interface ArticleService {
    // 发布文章
    Article createArticle(Article article);

    // 更新文章
    void updateArticle(Article article);

    // 删除文章
    void deleteArticle(Long id, Long userId);

    // 获取文章详情
    Article getArticleById(Long id);

    // 分页查询文章列表
    IPage<Article> getArticleList(Integer page, Integer size, String keyword);

    // 获取用户的文章列表
    IPage<Article> getUserArticles(Long userId, Integer page, Integer size);

    // 审核文章
    void reviewArticle(Long id, Integer status);

    // 添加管理员相关方法
    IPage<Article> getAdminArticleList(Integer page, Integer size, String keyword, Integer status);
    void adminUpdateArticle(Article article);
    void adminDeleteArticle(Long id);
}