package com.codepilot.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codepilot.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}