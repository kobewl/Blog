package com.codepilot.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("comment")
@ApiModel(description = "评论实体")
public class Comment {
    @ApiModelProperty("主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("文章ID")
    private Long articleId;

    @ApiModelProperty("评论用户ID")
    private Long userId;

    @ApiModelProperty("父评论ID")
    private Long parentId;

    @ApiModelProperty("回复用户ID")
    private Long toUserId;

    @ApiModelProperty("评论类型 0-文章评论 1-留言")
    private Integer type;

    @ApiModelProperty("状态 0-待审核 1-通过 2-驳回")
    private Integer status;

    @ApiModelProperty("审核意见")
    private String reviewReason;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("是否删除")
    @TableLogic
    private Integer isDeleted;
}