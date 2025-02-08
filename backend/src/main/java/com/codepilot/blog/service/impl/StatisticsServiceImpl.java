package com.codepilot.blog.service.impl;

import com.codepilot.blog.entity.Statistics;
import com.codepilot.blog.mapper.ArticleMapper;
import com.codepilot.blog.mapper.CommentMapper;
import com.codepilot.blog.mapper.UserMapper;
import com.codepilot.blog.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ArticleMapper articleMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String VIEW_COUNT_KEY = "article:view:";
    private static final String LIKE_COUNT_KEY = "article:like:";

    @Override
    public Statistics getStatistics() {
        Statistics statistics = new Statistics();
        String viewCount = String.valueOf(redisTemplate.opsForValue().get("blog:viewCount"));
        String todayViewCount = String.valueOf(redisTemplate.opsForValue().get("blog:todayViewCount"));
        statistics.setViewCount(Long.parseLong(viewCount != null && !viewCount.equals("null") ? viewCount : "0"));
        statistics.setTodayViewCount(Long.parseLong(todayViewCount != null && !todayViewCount.equals("null") ? todayViewCount : "0"));
        return statistics;
    }

    @Override
    public void incrementViewCount() {
        redisTemplate.opsForValue().increment("blog:viewCount");
        redisTemplate.opsForValue().increment("blog:todayViewCount");
    }

    @Override
    public void incrementViewCount(Long articleId) {
        redisTemplate.opsForValue().increment(VIEW_COUNT_KEY + articleId);
    }

    @Override
    public void incrementLikeCount(Long articleId) {
        redisTemplate.opsForValue().increment(LIKE_COUNT_KEY + articleId);
    }

    @Override
    public Long getViewCount(Long articleId) {
        Object count = redisTemplate.opsForValue().get(VIEW_COUNT_KEY + articleId);
        return count == null ? 0L : Long.parseLong(count.toString());
    }

    @Override
    public Long getLikeCount(Long articleId) {
        Object count = redisTemplate.opsForValue().get(LIKE_COUNT_KEY + articleId);
        return count == null ? 0L : Long.parseLong(count.toString());
    }
} 