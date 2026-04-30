package com.health.check.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPointsVO {

    private Long id;

    private Long userId;

    private Integer totalPoints;

    private Integer currentPoints;

    private Integer usedPoints;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
