package com.health.check.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AchievementVO {

    private Long id;

    private String achievementCode;

    private String name;

    private String description;

    private String icon;

    private String requirementType;

    private String requirementTypeName;

    private Integer requirementValue;

    private String requirementDescription;

    private Integer pointsReward;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean obtained;

    private LocalDate obtainDate;

    private Boolean isNew;
}
