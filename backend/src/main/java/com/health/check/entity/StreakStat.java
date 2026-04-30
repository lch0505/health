package com.health.check.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 连续打卡统计实体类
 */
@Data
@TableName("streak_stat")
public class StreakStat implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 连续打卡类型（wake_up-起床，sleep-睡觉）
     */
    private String streakType;

    /**
     * 当前连续打卡天数
     */
    private Integer currentStreak;

    /**
     * 最大连续打卡天数
     */
    private Integer maxStreak;

    /**
     * 最后打卡日期
     */
    private LocalDate lastCheckInDate;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
