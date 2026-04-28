package com.health.check.service;

import com.health.check.vo.DashboardVO;
import com.health.check.vo.MonthlySummaryVO;

public interface StatisticsService {
    DashboardVO getDashboard(Long userId);

    MonthlySummaryVO getMonthlySummary(Long userId, Integer year, Integer month);
}
