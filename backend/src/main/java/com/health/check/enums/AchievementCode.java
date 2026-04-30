package com.health.check.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AchievementCode {

    SLEEP_EARLY_MASTER("sleep_early_master", "早睡达人", "连续30天早睡打卡"),
    EXERCISE_MASTER("exercise_master", "运动坚持", "连续30天运动记录"),
    WATER_MASTER("water_master", "饮水自律", "连续30天饮水记录"),
    FULL_MONTH("full_month", "全月满卡", "自然月内每天都有打卡记录"),
    ;

    private final String code;
    private final String name;
    private final String description;

    public static AchievementCode getByCode(String code) {
        for (AchievementCode achievement : values()) {
            if (achievement.getCode().equals(code)) {
                return achievement;
            }
        }
        return null;
    }
}
