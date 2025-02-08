package com.codepilot.blog.aspect;

import com.codepilot.blog.annotation.RequireRole;
import com.codepilot.blog.annotation.RoleType;
import com.codepilot.blog.common.exception.BusinessException;
import com.codepilot.blog.entity.User;
import com.codepilot.blog.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 角色权限切面，用于拦截带有 @RequireRole 注解的方法或类，
 * 并验证当前用户是否具有所需的角色权限。
 */
@Aspect
@Component
public class RoleAspect {

    @Autowired
    private UserService userService;

    /**
     * 定义切入点，匹配所有带有 @RequireRole 注解的方法或类。
     */
    @Pointcut("@annotation(com.codepilot.blog.annotation.RequireRole) || @within(com.codepilot.blog.annotation.RequireRole)")
    public void rolePointCut() {
        // 此方法为空，仅用于定义切入点
    }

    /**
     * 环绕通知，在方法执行前后进行角色权限验证。
     *
     * @param point 切入点对象，包含目标方法的信息
     * @return 目标方法的返回值
     * @throws Throwable 如果验证失败或其他异常发生
     */
    @Around("rolePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 获取当前登录用户
        User user = userService.getCurrentUser(); // 不需要传参数
        if (user == null) {
            throw new BusinessException("用户未登录");
        }

        // 获取目标方法上的 @RequireRole 注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RequireRole methodAnnotation = method.getAnnotation(RequireRole.class);

        RoleType[] requiredRoles;
        if (methodAnnotation != null) {
            requiredRoles = methodAnnotation.value();
        } else {
            // 如果方法上没有注解，则尝试获取类上的注解
            RequireRole classAnnotation = point.getTarget().getClass().getAnnotation(RequireRole.class);
            if (classAnnotation != null) {
                requiredRoles = classAnnotation.value();
            } else {
                // 如果类上也没有注解，默认不允许访问
                throw new BusinessException("未配置角色权限");
            }
        }

        // 验证用户角色是否满足要求
        boolean hasPermission = Arrays.stream(requiredRoles)
                .anyMatch(role -> role.getCode() == user.getRole());

        if (!hasPermission) {
            // 如果用户没有所需的权限，抛出异常并提示需要哪些权限
            String requiredRoleDescs = Arrays.toString(
                    Arrays.stream(requiredRoles)
                            .map(RoleType::getDesc)
                            .toArray()
            );
            throw new BusinessException("权限不足，需要 " + requiredRoleDescs + " 权限");
        }

        // 如果权限验证通过，继续执行目标方法
        return point.proceed();
    }
}
