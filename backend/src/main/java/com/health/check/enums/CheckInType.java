package com.health.check.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CheckInType {

    WAKE_UP("wake_up", "起床"),
    SLEEP("sleep", "睡觉"),
    ;

    private final String code;
    private final String desc;

    public static CheckInType getByCode(String code) {
        for (CheckInType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
