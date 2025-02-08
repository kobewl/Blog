package com.codepilot.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codepilot.blog.entity.Article;
import com.codepilot.blog.mapper.ArticleMapper;
import com.codepilot.blog.service.ArticleService;
import com.codepilot.blog.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    @Transactional
    public Article createArticle(Article article) {
        // 检查必要字段
        if (article.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            throw new BusinessException("文章标题不能为空");
        }
        if (article.getContent() == null || article.getContent().trim().isEmpty()) {
            throw new BusinessException("文章内容不能为空");
        }
        
        try {
            // 初始化文章基本信息
            article.setViewCount(0);
            article.setLikeCount(0);
            article.setCommentCount(0);
            
            // 插入文章
            articleMapper.insert(article);
            
            // 返回完整的文章信息
            return articleMapper.selectById(article.getId());
        } catch (Exception e) {
            throw new BusinessException("创建文章失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateArticle(Article article) {
        Article existingArticle = articleMapper.selectById(article.getId());
        if (existingArticle == null) {
            throw new BusinessException("文章不存在");
        }
        if (!existingArticle.getUserId().equals(article.getUserId())) {
            throw new BusinessException("无权修改此文章");
        }
        
        try {
            articleMapper.updateById(article);
        } catch (Exception e) {
            throw new BusinessException("更新文章失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteArticle(Long id, Long userId) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        if (!article.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此文章");
        }
        try {
            articleMapper.deleteById(id);
        } catch (Exception e) {
            throw new BusinessException("删除文章失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Article getArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        try {
            // 增加浏览量
            article.setViewCount(article.getViewCount() + 1);
            articleMapper.updateById(article);
            return article;
        } catch (Exception e) {
            throw new BusinessException("获取文章失败：" + e.getMessage());
        }
    }

    @Override
    public IPage<Article> getArticleList(Integer page, Integer size, String keyword) {
        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        // 只查询已发布的文章
        wrapper.eq(Article::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Article::getTitle, keyword)
                    .or()
                    .like(Article::getContent, keyword);
        }
        wrapper.orderByDesc(Article::getCreateTime);
        return articleMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public IPage<Article> getUserArticles(Long userId, Integer page, Integer size) {
        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getUserId, userId)
                .orderByDesc(Article::getCreateTime);
        return articleMapper.selectPage(pageParam, wrapper);
    }

    @Override
    @Transactional
    public void reviewArticle(Long id, Integer status) {
        try {
            Article article = new Article();
            article.setId(id);
            article.setStatus(status);
            articleMapper.updateById(article);
        } catch (Exception e) {
            throw new BusinessException("审核文章失败：" + e.getMessage());
        }
    }

    @Override
    public IPage<Article> getAdminArticleList(Integer page, Integer size, String keyword, Integer status) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Article::getTitle, keyword)
                    .or()
                    .like(Article::getContent, keyword);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(Article::getStatus, status);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(Article::getCreateTime);
        
        return articleMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public void adminUpdateArticle(Article article) {
        try {
            if (articleMapper.updateById(article) != 1) {
                throw new BusinessException("更新文章失败");
            }
        } catch (Exception e) {
            throw new BusinessException("更新文章失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void adminDeleteArticle(Long id) {
        try {
            if (articleMapper.deleteById(id) != 1) {
                throw new BusinessException("删除文章失败");
            }
        } catch (Exception e) {
            throw new BusinessException("删除文章失败：" + e.getMessage());
        }
    }
}