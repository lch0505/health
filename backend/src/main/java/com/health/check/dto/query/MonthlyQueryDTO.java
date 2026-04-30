package com.health.check.dto.query;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;

/**
 * 月度统计查询DTO
 */
@Data
public class MonthlyQueryDTO {

    /**
     * 年份
     */
    @Min(value = 2000, message = "年份必须大于等于2000")
    @Max(value = 2100, message = "年份必须小于等于2100")
    private Integer year;

    /**
     * 月份
     */
    @Min(value = 1, message = "月份必须在1-12之间")
    @Max(value = 12, message = "月份必须在1-12之间")
    private Integer month;

    public Integer getYearOrDefault() {
        if (year == null) {
            return LocalDate.now().getYear();
        }
        return year;
    }

    public Integer getMonthOrDefault() {
        if (month == null) {
            return LocalDate.now().getMonthValue();
        }
        return month;
    }
}
