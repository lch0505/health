package com.health.check.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康记录实体类
 */
@Data
@TableName("health_record")
public class HealthRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 记录类型（exercise-运动，water-饮水，sleep_early-早睡）
     */
    private String recordType;

    /**
     * 数量（如运动次数、饮水量）
     */
    private Integer quantity;

    /**
     * 持续时长（分钟）
     */
    private Integer duration;

    /**
     * 状态（0-未完成，1-已完成）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer deleted;
}
