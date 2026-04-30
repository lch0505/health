package com.health.check.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 打卡请求DTO
 */
@Data
public class CheckInDTO {

    /**
     * 打卡类型（wake_up-起床，sleep-睡觉）
     */
    @NotBlank(message = "打卡类型不能为空")
    private String checkInType;

    /**
     * 备注
     */
    private String remark;
}
