package com.health.check.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserAchievementVO {

    private Long id;

    private Long userId;

    private String username;

    private String nickname;

    private Long achievementId;

    private String achievementCode;

    private String achievementName;

    private String achievementDescription;

    private String achievementIcon;

    private Integer achievementPointsReward;

    private LocalDate obtainDate;

    private Integer isNew;

    private LocalDateTime createTime;
}
