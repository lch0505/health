package com.health.check.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequirementType {

    STREAK("streak", "连续天数"),
    TOTAL("total", "总次数"),
    MONTHLY("monthly", "月度统计"),
    ;

    private final String code;
    private final String description;

    public static RequirementType getByCode(String code) {
        for (RequirementType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
