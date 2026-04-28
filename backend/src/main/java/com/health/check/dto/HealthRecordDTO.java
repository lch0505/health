package com.health.check.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class HealthRecordDTO {
    @NotBlank(message = "记录类型不能为空")
    private String recordType;

    private Integer quantity;

    private Integer duration;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private String remark;
}
