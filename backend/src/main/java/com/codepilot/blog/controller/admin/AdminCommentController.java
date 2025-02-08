package com.codepilot.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codepilot.blog.common.result.Result;
import com.codepilot.blog.entity.Comment;
import com.codepilot.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin/comment")
public class AdminCommentController extends AdminBaseController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/list")
    public Result<IPage<Comment>> getCommentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Result<String> permissionCheck = checkAdminPermission(request);
        if (permissionCheck != null) {
            return Result.error(permissionCheck.getMessage());
        }
        return Result.success(commentService.getAdminCommentList(page, size, type, status));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(
            @PathVariable Long id,
            HttpServletRequest request) {
        Result<String> permissionCheck = checkAdminPermission(request);
        if (permissionCheck != null) {
            return Result.error(permissionCheck.getMessage());
        }
        commentService.adminDeleteComment(id);
        return Result.success(null);
    }

    @PutMapping("/review/{id}")
    public Result<Void> reviewComment(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String reason,
            HttpServletRequest request) {
        Result<String> permissionCheck = checkAdminPermission(request);
        if (permissionCheck != null) {
            return Result.error(permissionCheck.getMessage());
        }
        commentService.adminReviewComment(id, status, reason);
        return Result.success(null);
    }
}