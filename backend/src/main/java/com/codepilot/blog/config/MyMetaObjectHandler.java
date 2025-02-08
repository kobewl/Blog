package com.codepilot.blog.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元对象处理器，用于自动填充创建时间和更新时间字段。
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 在插入记录时自动填充字段。
     *
     * @param metaObject 元对象，包含要插入的实体信息
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 自动填充 createTime 字段，使用当前时间作为默认值
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());

        // 自动填充 updateTime 字段，使用当前时间作为默认值
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 在更新记录时自动填充字段。
     *
     * @param metaObject 元对象，包含要更新的实体信息
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 自动填充 updateTime 字段，使用当前时间作为默认值
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
