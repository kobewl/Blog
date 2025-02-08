package com.codepilot.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codepilot.blog.common.result.Result;
import com.codepilot.blog.entity.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController extends AdminBaseController {

    @GetMapping("/list")
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        Result<String> permissionCheck = checkAdminPermission(request);
        if (permissionCheck != null) {
            return Result.error(permissionCheck.getMessage());
        }
        return Result.success(userService.getUserList(page, size, keyword));
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status,
            HttpServletRequest request) {
        Result<String> permissionCheck = checkAdminPermission(request);
        if (permissionCheck != null) {
            return Result.error(permissionCheck.getMessage());
        }
        userService.updateUserStatus(id, status);
        return Result.success(null);
    }

    @PutMapping("/role/{id}")
    public Result<Void> updateUserRole(
            @PathVariable Long id,
            @RequestParam Integer role,
            HttpServletRequest request) {
        Result<String> permissionCheck = checkAdminPermission(request);
        if (permissionCheck != null) {
            return Result.error(permissionCheck.getMessage());
        }
        userService.updateUserRole(id, role);
        return Result.success(null);
    }
}