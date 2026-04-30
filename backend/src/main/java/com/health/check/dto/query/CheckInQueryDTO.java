package com.health.check.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 打卡记录查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CheckInQueryDTO extends PageQueryDTO {

    /**
     * 用户ID（内部传递用，前端不需要传递）
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
