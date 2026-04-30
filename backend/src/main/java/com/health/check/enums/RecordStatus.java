package com.health.check.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecordStatus {

    NOT_COMPLETED(0, "未完成"),
    COMPLETED(1, "已完成"),
    ;

    private final Integer code;
    private final String desc;

    public static RecordStatus getByCode(Integer code) {
        for (RecordStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
