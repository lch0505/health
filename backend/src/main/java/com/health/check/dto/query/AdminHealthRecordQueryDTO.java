package com.health.check.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 管理员健康记录查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminHealthRecordQueryDTO extends PageQueryDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 记录类型
     */
    private String recordType;

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
