package com.codepilot.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codepilot.blog.common.exception.BusinessException;
import com.codepilot.blog.context.UserContext;
import com.codepilot.blog.dto.UserLoginDTO;
import com.codepilot.blog.dto.UserRegisterDTO;
import com.codepilot.blog.entity.User;
import com.codepilot.blog.mapper.UserMapper;
import com.codepilot.blog.service.UserService;
import com.codepilot.blog.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    private User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public void register(UserRegisterDTO registerDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, registerDTO.getUsername());
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, registerDTO.getEmail());
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        // 使用MD5加密密码
        user.setPassword(DigestUtils.md5DigestAsHex(registerDTO.getPassword().getBytes()));
        // 设置默认角色和状态
        user.setRole(0);
        user.setStatus(1);

        // 保存用户
        this.save(user);
    }

    @Override
    public String login(String username, String password) {
        // 1. 查询用户
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getIsDeleted, 0)
        );
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 2. 验证密码 (使用MD5加密)
        String encodedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!user.getPassword().equals(encodedPassword)) {
            throw new BusinessException("密码错误");
        }
        
        // 3. 生成token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        
        String token = JwtUtil.createToken(claims);
        log.info("用户登录成功，username: {}, token: {}", username, token);
        
        return token;
    }

    @Override
    public User getCurrentUser() {
        // 从ThreadLocal获取当前用户ID
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 清除敏感信息
        user.setPassword(null);
        
        return user;
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    @Override
    public IPage<User> getUserList(Integer page, Integer size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like("username", keyword)
                    .or()
                    .like("nickname", keyword)
                    .or()
                    .like("email", keyword);
        }
        wrapper.orderByDesc("create_time");
        return userMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void updateUserRole(Long id, Integer role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        userMapper.updateById(user);
    }

    @Override
    public void updateEmail(String email) {
        // 获取当前用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException("用户未登录");
        }

        // 验证邮箱格式
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BusinessException("邮箱格式不正确");
        }

        // 检查邮箱是否已被其他用户使用
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email)
                   .ne(User::getId, currentUser.getId());
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException("该邮箱已被其他用户使用");
        }

        // 更新邮箱
        User user = new User();
        user.setId(currentUser.getId());
        user.setEmail(email);
        if (!this.updateById(user)) {
            throw new BusinessException("更新邮箱失败");
        }
    }

    @Override
    public void updateUsername(String username) {
        // 获取当前用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException("用户未登录");
        }

        // 验证用户名格式
        if (username.length() < 2 || username.length() > 20) {
            throw new BusinessException("用户名长度应在2-20个字符之间");
        }

        // 检查用户名是否已被使用
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username)
                   .ne(User::getId, currentUser.getId());
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException("该用户名已被使用");
        }

        // 更新用户名
        User user = new User();
        user.setId(currentUser.getId());
        user.setUsername(username);
        if (!this.updateById(user)) {
            throw new BusinessException("更新用户名失败");
        }
    }

    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        // 获取当前用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException("用户未登录");
        }

        // 验证旧密码
        String encodedOldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        User fullUser = userMapper.selectById(currentUser.getId());
        if (!fullUser.getPassword().equals(encodedOldPassword)) {
            throw new BusinessException("旧密码不正确");
        }

        // 验证新密码格式
        if (newPassword.length() < 6) {
            throw new BusinessException("新密码长度不能小于6位");
        }

        // 更新密码
        User user = new User();
        user.setId(currentUser.getId());
        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        if (!this.updateById(user)) {
            throw new BusinessException("更新密码失败");
        }
    }
}