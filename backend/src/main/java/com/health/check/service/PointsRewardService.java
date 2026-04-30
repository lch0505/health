package com.health.check.service;

public interface PointsRewardService {
    void rewardDailyCheckIn(Long userId, String checkInType);

    void rewardContinuousStreak(Long userId, String checkInType, int currentStreak);

    void checkAndGrantAchievements(Long userId);

    void checkSleepEarlyAchievement(Long userId);

    void checkExerciseAchievement(Long userId);

    void checkWaterAchievement(Long userId);

    void checkFullMonthAchievement(Long userId, int year, int month);
}
