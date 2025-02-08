package com.codepilot.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codepilot.blog.common.result.Result;
import com.codepilot.blog.entity.Article;
import com.codepilot.blog.service.ArticleService;
import com.codepilot.blog.annotation.RequireRole;
import com.codepilot.blog.annotation.RoleType;
import com.codepilot.blog.context.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Api(tags = "文章管理")
@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @ApiOperation("创建文章")
    @RequireRole({RoleType.USER, RoleType.ADMIN, RoleType.VIP})
    @PostMapping("/create")
    public Result<Article> createArticle(
            @ApiParam("文章信息") @RequestBody Article article,
            HttpServletRequest request) {
        try {
            // 获取当前用户ID
            Long userId = UserContext.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            // 验证文章参数
            if (!StringUtils.hasText(article.getTitle())) {
                return Result.error("文章标题不能为空");
            }
            if (!StringUtils.hasText(article.getContent())) {
                return Result.error("文章内容不能为空");
            }
            
            // 设置用户ID和状态
            article.setUserId(userId);
            // 如果状态为空，默认为草稿状态
            if (article.getStatus() == null) {
                article.setStatus(0);
            }
            
            log.info("创建文章，title: {}, status: {}, userId: {}", 
                article.getTitle(), article.getStatus(), userId);
            
            // 创建文章
            Article created = articleService.createArticle(article);
            return Result.success(created);
        } catch (Exception e) {
            log.error("创建文章失败", e);
            return Result.error("创建文章失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除文章")
    @RequireRole({RoleType.USER, RoleType.ADMIN, RoleType.VIP})
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(
            @ApiParam("文章ID") @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        articleService.deleteArticle(id, userId);
        return Result.success(null);
    }

    @ApiOperation("更新文章")
    @RequireRole({RoleType.USER, RoleType.ADMIN, RoleType.VIP})
    @PutMapping("/update")
    public Result<Void> updateArticle(
            @ApiParam("文章信息") @RequestBody Article article,
            HttpServletRequest request) {
        try {
            Long userId = UserContext.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            // 验证文章参数
            if (!StringUtils.hasText(article.getTitle())) {
                return Result.error("文章标题不能为空");
            }
            if (!StringUtils.hasText(article.getContent())) {
                return Result.error("文章内容不能为空");
            }
            
            // 设置用户ID和状态
            article.setUserId(userId);
            // 如果状态为空，默认为草稿状态
            if (article.getStatus() == null) {
                article.setStatus(0);
            }
            
            log.info("更新文章，id: {}, title: {}, status: {}, userId: {}", 
                article.getId(), article.getTitle(), article.getStatus(), userId);
            
            articleService.updateArticle(article);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新文章失败", e);
            return Result.error("更新文章失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取文章详情")
    @RequireRole({RoleType.USER, RoleType.VIP, RoleType.ADMIN})
    @GetMapping("/{id}")
    public Result<Article> getArticle(
            @ApiParam("文章ID") @PathVariable Long id) {
        return Result.success(articleService.getArticleById(id));
    }

    @ApiOperation("获取文章列表")
    @RequireRole({RoleType.USER, RoleType.VIP, RoleType.ADMIN})
    @GetMapping("/list")
    public Result<IPage<Article>> getArticleList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword) {
        return Result.success(articleService.getArticleList(page, size, keyword));
    }

    @RequireRole({RoleType.USER, RoleType.VIP, RoleType.ADMIN})
    @GetMapping("/user/list")
    public Result<IPage<Article>> getUserArticles(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        return Result.success(articleService.getUserArticles(userId, page, size));
    }

    @RequireRole({RoleType.ADMIN})
    @PutMapping("/review/{id}")
    public Result<Void> reviewArticle(
            @PathVariable Long id,
            @RequestParam Integer status,
            HttpServletRequest request) {
        articleService.reviewArticle(id, status);
        return Result.success(null);
    }
}