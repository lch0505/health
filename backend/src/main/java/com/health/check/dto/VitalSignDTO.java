package com.health.check.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
public class VitalSignDTO {

    @DecimalMin(value = "0.0", message = "体重不能为负数")
    @DecimalMax(value = "500.0", message = "体重超出合理范围")
    private BigDecimal weight;

    @DecimalMin(value = "0.0", message = "体脂率不能为负数")
    @DecimalMax(value = "100.0", message = "体脂率不能超过100%")
    private BigDecimal bodyFat;

    @Min(value = 0, message = "收缩压不能为负数")
    @Max(value = 300, message = "收缩压超出合理范围")
    private Integer systolicPressure;

    @Min(value = 0, message = "舒张压不能为负数")
    @Max(value = 200, message = "舒张压超出合理范围")
    private Integer diastolicPressure;

    private String visionStatus;

    @Min(value = 1, message = "睡眠质量评分范围为1-5")
    @Max(value = 5, message = "睡眠质量评分范围为1-5")
    private Integer sleepQuality;

    private String remark;
}
