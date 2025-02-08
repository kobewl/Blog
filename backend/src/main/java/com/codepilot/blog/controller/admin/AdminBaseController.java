package com.codepilot.blog.controller.admin;

import com.codepilot.blog.common.result.Result;
import com.codepilot.blog.entity.User;
import com.codepilot.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class AdminBaseController {
    @Autowired
    protected UserService userService;

    protected Result<String> checkAdminPermission(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getCurrentUser();
        if (user.getRole() != 1) {
            return Result.error("无权限访问");
        }
        return null;
    }

    protected User getCurrentUser() {
        return userService.getCurrentUser(); // 不需要传参数
    }

    protected Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}