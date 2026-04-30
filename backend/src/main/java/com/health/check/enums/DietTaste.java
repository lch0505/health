package com.health.check.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DietTaste {

    LIGHT("light", "清淡"),
    MODERATE("moderate", "适中"),
    OILY("oily", "油腻"),
    ;

    private final String code;
    private final String desc;

    public static DietTaste getByCode(String code) {
        for (DietTaste taste : values()) {
            if (taste.getCode().equals(code)) {
                return taste;
            }
        }
        return null;
    }
}
