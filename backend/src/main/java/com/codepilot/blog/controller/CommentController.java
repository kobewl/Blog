package com.codepilot.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codepilot.blog.common.result.Result;
import com.codepilot.blog.entity.Comment;
import com.codepilot.blog.service.CommentService;
import com.codepilot.blog.context.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "评论接口")
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation("发表评论")
    @PostMapping("/create")
    public Result<Comment> createComment(@RequestBody Comment comment) {
        // 获取当前用户ID
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 设置评论作者
        comment.setUserId(userId);
        
        Comment created = commentService.createComment(comment);
        return Result.success(created);
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        commentService.deleteComment(id, userId);
        return Result.success(null);
    }

    @ApiOperation("获取文章评论列表")
    @GetMapping("/article/{articleId}")
    public Result<IPage<Comment>> getArticleComments(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(commentService.getArticleComments(articleId, page, size));
    }

    @ApiOperation("获取用户评论列表")
    @GetMapping("/user")
    public Result<IPage<Comment>> getUserComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        return Result.success(commentService.getUserComments(userId, page, size));
    }

    @ApiOperation("审核评论")
    @PutMapping("/review/{id}")
    public Result<Void> reviewComment(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String reason) {
        commentService.reviewComment(id, status, reason);
        return Result.success(null);
    }
}