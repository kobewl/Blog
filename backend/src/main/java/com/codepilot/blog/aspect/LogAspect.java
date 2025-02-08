package com.codepilot.blog.aspect;

import com.codepilot.blog.annotation.Log;
import com.codepilot.blog.entity.OperationLog;
import com.codepilot.blog.entity.User;
import com.codepilot.blog.mapper.OperationLogMapper;
import com.codepilot.blog.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private UserService userService;

    @Pointcut("@annotation(com.codepilot.blog.annotation.Log)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        try {
            // 执行方法
            Object result = point.proceed();
            // 执行时长(毫秒)
            long time = System.currentTimeMillis() - beginTime;
            // 保存日志
            saveLog(point, result, null, time);
            return result;
        } catch (Exception e) {
            // 执行时长(毫秒)
            long time = System.currentTimeMillis() - beginTime;
            // 保存日志
            saveLog(point, null, e, time);
            throw e;
        }
    }

    private void saveLog(ProceedingJoinPoint point, Object result, Exception e, long time) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);

        OperationLog operationLog = new OperationLog();

        // 获取当前用户信息
        User user = userService.getCurrentUser(); // 不再传递 userId 参数
        if (user != null) {
            operationLog.setUserId(user.getId());
            operationLog.setUsername(user.getUsername());
        }

        // 设置操作信息
        operationLog.setOperation(logAnnotation.value());
        operationLog.setMethod(method.getName());
        operationLog.setParams(point.getArgs().toString());
        
        // 获取请求信息
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        operationLog.setIp(request.getRemoteAddr());

        // 设置执行结果
        if (e != null) {
            operationLog.setStatus(1);
            operationLog.setErrorMsg(e.getMessage());
        } else {
            operationLog.setStatus(0);
        }

        // 保存日志
        operationLogMapper.insert(operationLog);
        
        // 打印日志
        logger.info("===================日志开始===================");
        logger.info("模块名称：{}", logAnnotation.module());
        logger.info("操作类型：{}", logAnnotation.operation());
        logger.info("操作说明：{}", logAnnotation.value());
        logger.info("请求方法：{}.{}", signature.getDeclaringTypeName(), method.getName());
        logger.info("执行时长：{} ms", time);
        logger.info("===================日志结束===================");
    }
}