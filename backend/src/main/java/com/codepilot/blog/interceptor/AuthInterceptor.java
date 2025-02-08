package com.codepilot.blog.interceptor;

import com.codepilot.blog.annotation.RequireRole;
import com.codepilot.blog.annotation.RoleType;
import com.codepilot.blog.common.exception.BusinessException;
import com.codepilot.blog.context.UserContext;
import com.codepilot.blog.entity.User;
import com.codepilot.blog.service.UserService;
import com.codepilot.blog.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行登录和注册接口
        String uri = request.getRequestURI();
        if (uri.contains("/login") || uri.contains("/register")) {
            return true;
        }

        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 获取token
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            throw new BusinessException("未登录");
        }

        // 去掉Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 解析token
            Claims claims = JwtUtil.parseToken(token);
            Long userId = claims.get("userId", Long.class);

            // 同时存入ThreadLocal和request属性
            UserContext.setCurrentUserId(userId);
            request.setAttribute("userId", userId);
            log.debug("用户认证成功，userId: {}", userId);

            // 获取方法上的注解
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RequireRole methodAnnotation = method.getAnnotation(RequireRole.class);

            // 获取类上的注解
            RequireRole classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireRole.class);

            // 如果方法和类上都没有注解，直接放行
            if (methodAnnotation == null && classAnnotation == null) {
                return true;
            }

            // 获取当前用户
            User user = userService.getCurrentUser();
            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            // 优先使用方法上的注解，如果没有则使用类上的注解
            RequireRole annotation = methodAnnotation != null ? methodAnnotation : classAnnotation;
            RoleType[] requiredRoles = annotation.value();

            // 验证用户角色是否满足要求
            boolean hasPermission = Arrays.stream(requiredRoles)
                    .anyMatch(role -> role.getCode() == user.getRole());

            if (!hasPermission) {
                String requiredRoleDescs = Arrays.toString(
                        Arrays.stream(requiredRoles)
                                .map(RoleType::getDesc)
                                .toArray());
                throw new BusinessException("权限不足，需要 " + requiredRoleDescs + " 权限");
            }

            return true;
        } catch (Exception e) {
            log.error("Token验证失败", e);
            throw new BusinessException("token无效或已过期");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        // 清理ThreadLocal
        UserContext.remove();
    }
}