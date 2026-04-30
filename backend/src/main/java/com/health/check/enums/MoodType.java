package com.health.check.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MoodType {

    VERY_HAPPY("very_happy", "非常开心"),
    HAPPY("happy", "开心"),
    NORMAL("normal", "一般"),
    SAD("sad", "难过"),
    ANGRY("angry", "生气"),
    ;

    private final String code;
    private final String desc;

    public static MoodType getByCode(String code) {
        for (MoodType mood : values()) {
            if (mood.getCode().equals(code)) {
                return mood;
            }
        }
        return null;
    }
}
