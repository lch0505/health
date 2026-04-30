package com.health.check.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 管理员打卡记录查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminCheckInQueryDTO extends PageQueryDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 打卡类型
     */
    private String checkInType;

    /**
     * 开始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
