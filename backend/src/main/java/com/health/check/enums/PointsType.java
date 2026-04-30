package com.health.check.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointsType {

    DAILY_CHECK_IN("daily_check_in", "每日打卡", 10),
    CONTINUOUS_STREAK_7("continuous_streak_7", "连续7天打卡", 50),
    CONTINUOUS_STREAK_15("continuous_streak_15", "连续15天打卡", 100),
    CONTINUOUS_STREAK_30("continuous_streak_30", "连续30天打卡", 200),
    GOAL_COMPLETE("goal_complete", "完成目标", 20),
    ACHIEVEMENT_REWARD("achievement_reward", "勋章奖励", 0),
    EXCHANGE("exchange", "积分兑换", 0),
    ;

    private final String code;
    private final String description;
    private final Integer defaultPoints;

    public static PointsType getByCode(String code) {
        for (PointsType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
