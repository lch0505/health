package com.health.check.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("points_record")
public class PointsRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer points;

    private String pointsType;

    private String description;

    private Long referenceId;

    private LocalDate recordDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
