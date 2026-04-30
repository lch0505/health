package com.health.check.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VisionStatus {

    EXCELLENT("excellent", "优秀"),
    GOOD("good", "良好"),
    NORMAL("normal", "一般"),
    POOR("poor", "较差"),
    ;

    private final String code;
    private final String desc;

    public static VisionStatus getByCode(String code) {
        for (VisionStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
