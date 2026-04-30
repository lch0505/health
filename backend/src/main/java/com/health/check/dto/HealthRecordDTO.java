package com.health.check.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 健康记录请求DTO
 */
@Data
public class HealthRecordDTO {

    /**
     * 记录类型（exercise-运动，water-饮水，sleep_early-早睡）
     */
    @NotBlank(message = "记录类型不能为空")
    private String recordType;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 持续时长（分钟）
     */
    private Integer duration;

    /**
     * 状态（0-未完成，1-已完成）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
