package com.health.check.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DietRecordDTO {

    @NotBlank(message = "餐次类型不能为空")
    private String mealType;

    private String dietTaste;

    private Integer avoidFoodCompliance;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private String remark;
}
