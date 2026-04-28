package com.health.check.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CheckInDTO {
    @NotBlank(message = "打卡类型不能为空")
    private String checkInType;

    private String remark;
}
