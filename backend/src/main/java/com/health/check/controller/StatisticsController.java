package com.health.check.controller;

import com.health.check.common.Result;
import com.health.check.service.StatisticsService;
import com.health.check.service.UserService;
import com.health.check.vo.DashboardVO;
import com.health.check.vo.MonthlySummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/user/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private UserService userService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getByUsername(username).getId();
    }

    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        Long userId = getCurrentUserId();
        DashboardVO dashboard = statisticsService.getDashboard(userId);
        return Result.success(dashboard);
    }

    @GetMapping("/monthly-summary")
    public Result<MonthlySummaryVO> getMonthlySummary(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        Long userId = getCurrentUserId();

        if (year == null || month == null) {
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getMonthValue();
        }

        MonthlySummaryVO summary = statisticsService.getMonthlySummary(userId, year, month);
        return Result.success(summary);
    }
}
