package com.codepilot.blog.controller.admin;

import com.codepilot.blog.annotation.Log;
import com.codepilot.blog.common.result.Result;
import com.codepilot.blog.entity.Statistics;
import com.codepilot.blog.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin/statistics")
public class AdminStatisticsController extends AdminBaseController {
    @Autowired
    private StatisticsService statisticsService;

    @Log("查看统计数据")
    @GetMapping
    public Result<Statistics> getStatistics(HttpServletRequest request) {
        Result<String> permissionCheck = checkAdminPermission(request);
        if (permissionCheck != null) {
            return Result.error(permissionCheck.getMessage());
        }
        return Result.success(statisticsService.getStatistics());
    }
} 