package com.health.check.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_achievement")
public class UserAchievement implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long achievementId;

    private String achievementCode;

    private LocalDate obtainDate;

    private Integer isNew;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
