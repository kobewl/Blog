package com.codepilot.blog.entity;

import lombok.Data;

@Data
public class Statistics {
    private Long userCount; // 用户总数
    private Long articleCount; // 文章总数
    private Long commentCount; // 评论总数
    private Long todayUserCount; // 今日新增用户
    private Long todayArticleCount; // 今日新增文章
    private Long todayCommentCount; // 今日新增评论
    private Long viewCount; // 总访问量
    private Long todayViewCount; // 今日访问量
}