package com.codepilot.blog.service;

import com.codepilot.blog.entity.Statistics;

public interface StatisticsService {
    Statistics getStatistics();
    void incrementViewCount();
    void incrementViewCount(Long articleId);
    void incrementLikeCount(Long articleId);
    Long getViewCount(Long articleId);
    Long getLikeCount(Long articleId);
}