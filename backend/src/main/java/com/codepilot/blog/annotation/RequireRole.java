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
