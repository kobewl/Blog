package com.codepilot.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String operation; // 操作类型

    private String method; // 请求方法

    private String params; // 请求参数

    private String ip; // 操作IP

    private Integer status; // 操作状态 0-成功 1-失败

    private String errorMsg; // 错误信息

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer isDeleted;
}