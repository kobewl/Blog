package com.codepilot.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.codepilot.blog.dto.UserLoginDTO;
import com.codepilot.blog.dto.UserRegisterDTO;
import com.codepilot.blog.entity.User;

public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    void register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     */
    String login(String username, String password);

    /**
     * 获取当前登录用户
     */
    User getCurrentUser();

    void updateUser(User user);

    // 获取用户列表（管理员）
    IPage<User> getUserList(Integer page, Integer size, String keyword);

    // 更新用户状态（管理员）
    void updateUserStatus(Long id, Integer status);

    // 更新用户角色（管理员）
    void updateUserRole(Long id, Integer role);
}