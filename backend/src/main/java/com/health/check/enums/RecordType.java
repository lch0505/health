package com.health.check.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecordType {

    EXERCISE("exercise", "运动"),
    WATER("water", "饮水"),
    SLEEP_EARLY("sleep_early", "早睡"),
    ;

    private final String code;
    private final String desc;

    public static RecordType getByCode(String code) {
        for (RecordType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
