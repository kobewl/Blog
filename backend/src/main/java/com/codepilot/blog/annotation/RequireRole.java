package com.codepilot.blog.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    /**
     * 需要的角色，默认需要管理员角色
     */
    RoleType[] value() default {RoleType.ADMIN};
}

/**
 * 角色枚举
 */
/*public enum RoleType {
    ADMIN(1, "管理员"),
    VIP(2, "VIP用户"),
    USER(0, "普通用户");

    private final int code;
    private final String desc;

    RoleType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }*/
/*
} */
