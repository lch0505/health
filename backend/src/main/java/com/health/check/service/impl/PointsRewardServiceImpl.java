package com.health.check.service.impl;

import com.health.check.entity.Achievement;
import com.health.check.entity.HealthRecord;
import com.health.check.entity.UserAchievement;
import com.health.check.enums.AchievementCode;
import com.health.check.enums.CheckInType;
import com.health.check.enums.PointsType;
import com.health.check.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class PointsRewardServiceImpl implements PointsRewardService {

    @Autowired
    private UserPointsService userPointsService;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private UserAchievementService userAchievementService;

    @Autowired
    private HealthRecordService healthRecordService;

    @Autowired
    private CheckInService checkInService;

    @Override
    @Transactional
    public void rewardDailyCheckIn(Long userId, String checkInType) {
        String typeName = CheckInType.WAKE_UP.getCode().equals(checkInType) ? "起床" : "睡觉";
        userPointsService.addPoints(
                userId,
                PointsType.DAILY_CHECK_IN.getDefaultPoints(),
                PointsType.DAILY_CHECK_IN.getCode(),
                "每日" + typeName + "打卡奖励",
                null
        );
    }

    @Override
    @Transactional
    public void rewardContinuousStreak(Long userId, String checkInType, int currentStreak) {
        PointsType rewardType = null;
        String description = null;

        if (currentStreak == 7) {
            rewardType = PointsType.CONTINUOUS_STREAK_7;
            description = "连续7天打卡奖励";
        } else if (currentStreak == 15) {
            rewardType = PointsType.CONTINUOUS_STREAK_15;
            description = "连续15天打卡奖励";
        } else if (currentStreak == 30) {
            rewardType = PointsType.CONTINUOUS_STREAK_30;
            description = "连续30天打卡奖励";
        }

        if (rewardType != null) {
            userPointsService.addPoints(
                    userId,
                    rewardType.getDefaultPoints(),
                    rewardType.getCode(),
                    description,
                    null
            );
        }
    }

    @Override
    @Transactional
    public void checkAndGrantAchievements(Long userId) {
        checkSleepEarlyAchievement(userId);
        checkExerciseAchievement(userId);
        checkWaterAchievement(userId);
    }

    @Override
    @Transactional
    public void checkSleepEarlyAchievement(Long userId) {
        Achievement achievement = achievementService.getByCode(AchievementCode.SLEEP_EARLY_MASTER.getCode());
        if (achievement == null) {
            return;
        }

        if (userAchievementService.hasAchievement(userId, achievement.getId())) {
            return;
        }

        int currentStreak = getContinuousHealthRecordStreak(userId, "sleep_early");
        if (currentStreak >= achievement.getRequirementValue()) {
            grantAchievementWithPoints(userId, achievement);
        }
    }

    @Override
    @Transactional
    public void checkExerciseAchievement(Long userId) {
        Achievement achievement = achievementService.getByCode(AchievementCode.EXERCISE_MASTER.getCode());
        if (achievement == null) {
            return;
        }

        if (userAchievementService.hasAchievement(userId, achievement.getId())) {
            return;
        }

        int currentStreak = getContinuousHealthRecordStreak(userId, "exercise");
        if (currentStreak >= achievement.getRequirementValue()) {
            grantAchievementWithPoints(userId, achievement);
        }
    }

    @Override
    @Transactional
    public void checkWaterAchievement(Long userId) {
        Achievement achievement = achievementService.getByCode(AchievementCode.WATER_MASTER.getCode());
        if (achievement == null) {
            return;
        }

        if (userAchievementService.hasAchievement(userId, achievement.getId())) {
            return;
        }

        int currentStreak = getContinuousHealthRecordStreak(userId, "water");
        if (currentStreak >= achievement.getRequirementValue()) {
            grantAchievementWithPoints(userId, achievement);
        }
    }

    @Override
    @Transactional
    public void checkFullMonthAchievement(Long userId, int year, int month) {
        Achievement achievement = achievementService.getByCode(AchievementCode.FULL_MONTH.getCode());
        if (achievement == null) {
            return;
        }

        if (userAchievementService.hasAchievement(userId, achievement.getId())) {
            return;
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        LocalDate today = LocalDate.now();

        if (today.isBefore(lastDay)) {
            return;
        }

        boolean fullMonth = checkFullMonthCheckIn(userId, firstDay, lastDay);
        if (fullMonth) {
            grantAchievementWithPoints(userId, achievement);
        }
    }

    private void grantAchievementWithPoints(Long userId, Achievement achievement) {
        UserAchievement userAchievement = userAchievementService.grantAchievement(
                userId,
                achievement.getId(),
                achievement.getAchievementCode(),
                LocalDate.now()
        );

        if (userAchievement != null && achievement.getPointsReward() > 0) {
            userPointsService.addPoints(
                    userId,
                    achievement.getPointsReward(),
                    PointsType.ACHIEVEMENT_REWARD.getCode(),
                    "获得勋章【" + achievement.getName() + "】奖励",
                    userAchievement.getId()
            );
        }
    }

    private int getContinuousHealthRecordStreak(Long userId, String recordType) {
        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate checkDate = today;

        while (true) {
            HealthRecord record = healthRecordService.getRecordByDateAndType(userId, checkDate, recordType);
            if (record != null && record.getStatus() == 1) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }

    private boolean checkFullMonthCheckIn(Long userId, LocalDate firstDay, LocalDate lastDay) {
        LocalDate current = firstDay;
        while (!current.isAfter(lastDay)) {
            boolean hasCheckIn = checkInService.hasAnyCheckInOnDate(userId, current);
            if (!hasCheckIn) {
                return false;
            }
            current = current.plusDays(1);
        }
        return true;
    }
}
