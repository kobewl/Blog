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
}