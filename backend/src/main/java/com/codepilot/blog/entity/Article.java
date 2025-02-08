package com.codepilot.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("article")
@ApiModel(description = "文章实体")
public class Article {
    @ApiModelProperty("主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("封面图片")
    private String cover;

    @ApiModelProperty("分类ID")
    private Long categoryId;

    @ApiModelProperty("作者ID")
    private Long userId;

    @ApiModelProperty("浏览量")
    private Integer viewCount;

    @ApiModelProperty("点赞量")
    private Integer likeCount;

    @ApiModelProperty("评论量")
    private Integer commentCount;

    @ApiModelProperty("状态 0-草稿 1-已发布")
    private Integer status;

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