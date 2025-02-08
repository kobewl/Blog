package com.codepilot.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codepilot.blog.common.exception.BusinessException;
import com.codepilot.blog.entity.Comment;
import com.codepilot.blog.mapper.CommentMapper;
import com.codepilot.blog.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    @Transactional
    public Comment createComment(Comment comment) {
        // 验证评论内容
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new BusinessException("评论内容不能为空");
        }
        
        // 设置评论状态为已通过（可以根据需求修改为需要审核）
        comment.setStatus(1);
        comment.setType(0);
        
        // 保存评论
        this.save(comment);
        
        return comment;
    }

    @Override
    @Transactional
    public void deleteComment(Long id, Long userId) {
        Comment comment = this.getById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        // 检查是否有权限删除（评论作者或管理员）
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此评论");
        }
        
        this.removeById(id);
    }

    @Override
    public IPage<Comment> getArticleComments(Long articleId, Integer page, Integer size) {
        Page<Comment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Comment::getArticleId, articleId)
               .eq(Comment::getStatus, 1)  // 只查询已通过的评论
               .orderByDesc(Comment::getCreateTime);
        
        return this.page(pageParam, wrapper);
    }

    @Override
    public IPage<Comment> getUserComments(Long userId, Integer page, Integer size) {
        Page<Comment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Comment::getUserId, userId)
               .orderByDesc(Comment::getCreateTime);
        
        return this.page(pageParam, wrapper);
    }

    @Override
    @Transactional
    public void reviewComment(Long id, Integer status, String reason) {
        Comment comment = this.getById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        comment.setStatus(status);
        comment.setReviewReason(reason);
        this.updateById(comment);
    }

    @Override
    public IPage<Comment> getMessageList(Integer page, Integer size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getType, 1)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }

    @Override
    public IPage<Comment> getPendingComments(Integer page, Integer size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getStatus, 0)
                .orderByDesc(Comment::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }

    @Override
    public IPage<Comment> getAdminCommentList(Integer page, Integer size, Integer articleId, Integer status) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (articleId != null) {
            wrapper.eq(Comment::getArticleId, articleId);
        }
        if (status != null) {
            wrapper.eq(Comment::getStatus, status);
        }
        wrapper.orderByDesc(Comment::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }

    @Override
    public void adminDeleteComment(Long id) {
        if (!this.removeById(id)) {
            throw new BusinessException("删除评论失败");
        }
    }

    @Override
    @Transactional
    public void adminReviewComment(Long id, Integer status, String reason) {
        Comment comment = this.getById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        comment.setStatus(status);
        comment.setReviewReason(reason);
        if (!this.updateById(comment)) {
            throw new BusinessException("审核评论失败");
        }
    }
}