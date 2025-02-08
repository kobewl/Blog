package com.codepilot.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codepilot.blog.common.result.Result;
import com.codepilot.blog.entity.Article;
import com.codepilot.blog.service.ArticleService;
import com.codepilot.blog.annotation.RequireRole;
import com.codepilot.blog.annotation.RoleType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员文章管理")
@RestController
@RequestMapping("/admin/article")
@RequireRole(RoleType.ADMIN)
public class AdminArticleController {
    
    @Autowired
    private ArticleService articleService;

    @ApiOperation("获取文章列表")
    @GetMapping("/list")
    public Result<IPage<Article>> getArticleList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        return Result.success(articleService.getAdminArticleList(page, size, keyword, status));
    }

    @ApiOperation("更新文章")
    @PutMapping("/update")
    public Result<Void> updateArticle(@ApiParam("文章信息") @RequestBody Article article) {
        articleService.adminUpdateArticle(article);
        return Result.success(null);
    }

    @ApiOperation("删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@ApiParam("文章ID") @PathVariable Long id) {
        articleService.adminDeleteArticle(id);
        return Result.success(null);
    }

    @ApiOperation("审核文章")
    @PutMapping("/review/{id}")
    public Result<Void> reviewArticle(
            @ApiParam("文章ID") @PathVariable Long id,
            @ApiParam("审核状态") @RequestParam Integer status) {
        articleService.reviewArticle(id, status);
        return Result.success(null);
    }
}
