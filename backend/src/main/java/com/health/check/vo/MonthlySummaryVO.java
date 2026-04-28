package com.health.check.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MonthlySummaryVO {
    private Integer year;
    private Integer month;
    private Integer totalDays;
    private Integer checkInDays;
    private Integer maxStreak;
    private Integer averageStreak;
    private Integer completionRate;
    private List<Map<String, Object>> dailyDetails;
    private Map<String, Object> typeStatistics;
}
