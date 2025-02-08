package com.codepilot.blog.controller;

import com.codepilot.blog.common.Result;
import com.codepilot.blog.dto.UserRegisterDTO;
import com.codepilot.blog.entity.User;
import com.codepilot.blog.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success(null);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody User user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        return Result.success(token);
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        User user = userService.getCurrentUser();
        return Result.success(user);
    }

    @ApiOperation("更新用户邮箱")
    @PutMapping("/update/email")
    public Result<Void> updateEmail(@RequestParam String email) {
        userService.updateEmail(email);
        return Result.success(null);
    }

    @ApiOperation("更新用户名")
    @PutMapping("/update/username")
    public Result<Void> updateUsername(@RequestParam String username) {
        userService.updateUsername(username);
        return Result.success(null);
    }

    @ApiOperation("更新密码")
    @PutMapping("/update/password")
    public Result<Void> updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        userService.updatePassword(oldPassword, newPassword);
        return Result.success(null);
    }
}