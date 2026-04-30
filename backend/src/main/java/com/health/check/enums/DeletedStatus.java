package com.health.check.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeletedStatus {

    NOT_DELETED(0, "未删除"),
    DELETED(1, "已删除"),
    ;

    private final Integer code;
    private final String desc;

    public static DeletedStatus getByCode(Integer code) {
        for (DeletedStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
