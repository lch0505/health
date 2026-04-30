package com.health.check.controller;

import com.health.check.common.Result;
import com.health.check.dto.query.MonthlyQueryDTO;
import com.health.check.service.StatisticsService;
import com.health.check.util.SecurityUtils;
import com.health.check.vo.DashboardVO;
import com.health.check.vo.MonthlySummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        Long userId = SecurityUtils.getCurrentUserId();
        DashboardVO dashboard = statisticsService.getDashboard(userId);
        return Result.success(dashboard);
    }

    @GetMapping("/monthly-summary")
    public Result<MonthlySummaryVO> getMonthlySummary(@Validated MonthlyQueryDTO query) {
        Long userId = SecurityUtils.getCurrentUserId();
        MonthlySummaryVO summary = statisticsService.getMonthlySummary(userId, query.getYearOrDefault(), query.getMonthOrDefault());
        return Result.success(summary);
    }
}
