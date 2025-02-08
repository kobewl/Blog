package com.codepilot.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.codepilot.blog.entity.Comment;

public interface CommentService extends IService<Comment> {
    
    /**
     * 创建评论
     */
    Comment createComment(Comment comment);
    
    /**
     * 删除评论
     */
    void deleteComment(Long id, Long userId);
    
    /**
     * 获取文章评论列表
     */
    IPage<Comment> getArticleComments(Long articleId, Integer page, Integer size);
    
    /**
     * 获取用户评论列表
     */
    IPage<Comment> getUserComments(Long userId, Integer page, Integer size);
    
    /**
     * 审核评论
     */
    void reviewComment(Long id, Integer status, String reason);

    // 获取所有留言列表
    IPage<Comment> getMessageList(Integer page, Integer size);

    // 获取待审核的评论和留言列表
    IPage<Comment> getPendingComments(Integer page, Integer size);

    // 管理员相关方法
    IPage<Comment> getAdminCommentList(Integer page, Integer size, Integer articleId, Integer status);
    void adminDeleteComment(Long id);
    void adminReviewComment(Long id, Integer status, String reason);
}