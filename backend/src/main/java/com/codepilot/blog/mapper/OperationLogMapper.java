package com.codepilot.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codepilot.blog.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}