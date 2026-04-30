package com.health.check.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SleepQuality {

    EXCELLENT(5, "非常好"),
    GOOD(4, "良好"),
    NORMAL(3, "一般"),
    POOR(2, "较差"),
    VERY_POOR(1, "非常差"),
    ;

    private final Integer code;
    private final String desc;

    public static SleepQuality getByCode(Integer code) {
        for (SleepQuality quality : values()) {
            if (quality.getCode().equals(code)) {
                return quality;
            }
        }
        return null;
    }
}
