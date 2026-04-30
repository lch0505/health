package com.health.check.common.constants;

public final class StatisticsConstants {

    public static final int WEEK_DAYS = 7;

    public static final int TOTAL_HEALTH_TYPES = 3;

    public static final String[] ALL_TYPES = {"wake_up", "sleep", "exercise", "water", "sleep_early"};
    public static final String[] ALL_TYPE_NAMES = {"起床", "睡觉", "运动", "饮水", "早睡"};

    public static final String[] CHECK_IN_TYPES = {"wake_up", "sleep"};
    public static final String[] CHECK_IN_TYPE_NAMES = {"起床", "睡觉"};

    public static final String[] RECORD_TYPES = {"exercise", "water", "sleep_early"};
    public static final String[] RECORD_TYPE_NAMES = {"运动", "饮水", "早睡"};

    private StatisticsConstants() {
    }
}
