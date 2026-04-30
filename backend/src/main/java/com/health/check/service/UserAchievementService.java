package com.health.check.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.entity.UserAchievement;

import java.time.LocalDate;
import java.util.List;

public interface UserAchievementService extends IService<UserAchievement> {
    List<UserAchievement> getByUserId(Long userId);

    boolean hasAchievement(Long userId, Long achievementId);

    UserAchievement grantAchievement(Long userId, Long achievementId, String achievementCode, LocalDate obtainDate);

    Page<UserAchievement> getUserAchievementPage(Integer page, Integer size, Long userId, Long achievementId, LocalDate startDate, LocalDate endDate);

    long countNewAchievements(Long userId);

    void markAsRead(Long userId, Long userAchievementId);
}
