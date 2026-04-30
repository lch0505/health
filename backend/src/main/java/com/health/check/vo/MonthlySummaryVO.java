package com.health.check.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 月度统计数据VO
 */
@Data
public class MonthlySummaryVO {

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份
     */
    private Integer month;

    /**
     * 当月总天数
     */
    private Integer totalDays;

    /**
     * 当月打卡天数
     */
    private Integer checkInDays;

    /**
     * 最大连续打卡天数
     */
    private Integer maxStreak;

    /**
     * 平均连续打卡天数
     */
    private Integer averageStreak;

    /**
     * 完成率
     */
    private Integer completionRate;

    /**
     * 每日详情列表
     */
    private List<Map<String, Object>> dailyDetails;

    /**
     * 类型统计数据
     */
    private Map<String, Object> typeStatistics;
}
