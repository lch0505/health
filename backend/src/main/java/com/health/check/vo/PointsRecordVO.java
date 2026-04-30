package com.health.check.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PointsRecordVO {

    private Long id;

    private Long userId;

    private String username;

    private String nickname;

    private Integer points;

    private String pointsType;

    private String pointsTypeName;

    private String description;

    private Long referenceId;

    private LocalDate recordDate;

    private LocalDateTime createTime;
}
