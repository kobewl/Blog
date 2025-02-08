package com.codepilot.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codepilot.blog.entity.Comment;
import com.codepilot.blog.entity.User;
import com.codepilot.blog.mapper.CommentMapper;
import com.codepilot.blog.mapper.UserMapper;
import com.codepilot.blog.service.CommentService;
import com.codepilot.blog.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Comment createComment(Comment comment) {
        // VIP用户发表的评论和留言直接通过审核
        User user = userMapper.selectById(comment.getUserId());
        if (user.getRole() == 2) { // VIP用户
            comment.setStatus(1);
        } else {
            comment.setStatus(0); // 待审核
        }
        commentMapper.insert(comment);
        return comment;
    }

    @Override
    public void deleteComment(Long id, Long userId) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此评论");
        }
        commentMapper.deleteById(id);
    }

    @Override
    public IPage<Comment> getCommentList(Long articleId, Integer page, Integer size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, articleId)
                .eq(Comment::getType, 0)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime);
        return commentMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public IPage<Comment> getMessageList(Integer page, Integer size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getType, 1)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime);
        return commentMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public IPage<Comment> getUserComments(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getUserId, userId)
                .orderByDesc(Comment::getCreateTime);
        return commentMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public void reviewComment(Long id, Integer status) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus(status);
        commentMapper.updateById(comment);
    }

    @Override
    public IPage<Comment> getPendingComments(Integer page, Integer size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getStatus, 0)
                .orderByDesc(Comment::getCreateTime);
        return commentMapper.selectPage(new Page<>(page, size), wrapper);
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
        return commentMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public void adminDeleteComment(Long id) {
        if (commentMapper.deleteById(id) != 1) {
            throw new BusinessException("删除评论失败");
        }
    }

    @Override
    public void adminReviewComment(Long id, Integer status, String reason) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus(status);
        comment.setReviewReason(reason);
        if (commentMapper.updateById(comment) != 1) {
            throw new BusinessException("审核评论失败");
        }
    }
}