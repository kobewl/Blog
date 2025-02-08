package com.codepilot.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codepilot.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
} 