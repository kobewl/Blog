package com.codepilot.blog.annotation;

public enum RoleType {
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
    }
} 