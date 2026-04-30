package com.health.check.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘数据VO
 */
@Data
public class DashboardVO {

    /**
     * 当前连续打卡天数
     */
    private Integer currentStreak;

    /**
     * 最大连续打卡天数
     */
    private Integer maxStreak;

    /**
     * 今日打卡数量
     */
    private Integer todayCheckInCount;

    /**
     * 今日已完成健康记录数量
     */
    private Integer todayCompletedRecords;

    /**
     * 最近7天打卡数据
     */
    private List<Map<String, Object>> recentCheckIns;

    /**
     * 本周统计数据
     */
    private List<Map<String, Object>> weekStatistics;
}
