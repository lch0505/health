package com.health.check.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {
    private Integer currentStreak;
    private Integer maxStreak;
    private Integer todayCheckInCount;
    private Integer todayCompletedRecords;
    private List<Map<String, Object>> recentCheckIns;
    private List<Map<String, Object>> weekStatistics;
}
