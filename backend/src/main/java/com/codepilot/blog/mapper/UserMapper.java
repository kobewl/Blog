package com.codepilot.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codepilot.blog.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // MyBatis-Plus 会自动实现基础的CRUD方法
    // 如果需要自定义复杂查询，可以在这里添加方法
}