package com.codepilot.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codepilot.blog.common.result.Result;
import com.codepilot.blog.entity.Comment;
import com.codepilot.blog.entity.User;
import com.codepilot.blog.service.CommentService;
import com.codepilot.blog.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "评论管理")
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @ApiOperation("发表评论")
    @PostMapping
    public Result<Comment> createComment(@RequestBody Comment comment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        comment.setUserId(userId);
        return Result.success(commentService.createComment(comment));
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        commentService.deleteComment(id, userId);
        return Result.success(null);
    }

    @ApiOperation("获取文章评论列表")
    @GetMapping("/article/{articleId}")
    public Result<IPage<Comment>> getArticleComments(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(commentService.getCommentList(articleId, page, size));
    }

    @ApiOperation("获取用户评论列表")
    @GetMapping("/user")
    public Result<IPage<Comment>> getUserComments(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(commentService.getUserComments(userId, page, size));
    }

    @GetMapping("/message/list")
    public Result<IPage<Comment>> getMessageList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(commentService.getMessageList(page, size));
    }

    @GetMapping("/pending")
    public Result<IPage<Comment>> getPendingComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getCurrentUser();
        if (user.getRole() != 1) {
            return Result.error("无权限访问");
        }
        return Result.success(commentService.getPendingComments(page, size));
    }

    @PutMapping("/review/{id}")
    public Result<Void> reviewComment(
            @PathVariable Long id,
            @RequestParam Integer status,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getCurrentUser();
        if (user.getRole() != 1) {
            return Result.error("无权限操作");
        }
        commentService.reviewComment(id, status);
        return Result.success(null);
    }
}