package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.health.check.entity.CheckIn;
import com.health.check.entity.HealthRecord;
import com.health.check.entity.StreakStat;
import com.health.check.service.CheckInService;
import com.health.check.service.HealthRecordService;
import com.health.check.service.StatisticsService;
import com.health.check.service.StreakStatService;
import com.health.check.vo.DashboardVO;
import com.health.check.vo.MonthlySummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StreakStatService streakStatService;

    @Autowired
    private CheckInService checkInService;

    @Autowired
    private HealthRecordService healthRecordService;

    @Override
    public DashboardVO getDashboard(Long userId) {
        DashboardVO dashboard = new DashboardVO();

        List<StreakStat> stats = streakStatService.getByUserId(userId);
        int currentStreak = 0;
        int maxStreak = 0;
        for (StreakStat stat : stats) {
            if (stat.getCurrentStreak() > currentStreak) {
                currentStreak = stat.getCurrentStreak();
            }
            if (stat.getMaxStreak() > maxStreak) {
                maxStreak = stat.getMaxStreak();
            }
        }
        dashboard.setCurrentStreak(currentStreak);
        dashboard.setMaxStreak(maxStreak);

        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<CheckIn> checkInWrapper = new LambdaQueryWrapper<>();
        checkInWrapper.eq(CheckIn::getUserId, userId)
                .eq(CheckIn::getCheckInDate, today)
                .eq(CheckIn::getDeleted, 0);
        int todayCheckInCount = (int) checkInService.count(checkInWrapper);
        dashboard.setTodayCheckInCount(todayCheckInCount);

        LambdaQueryWrapper<HealthRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(HealthRecord::getUserId, userId)
                .eq(HealthRecord::getRecordDate, today)
                .eq(HealthRecord::getStatus, 1)
                .eq(HealthRecord::getDeleted, 0);
        int todayCompletedRecords = (int) healthRecordService.count(recordWrapper);
        dashboard.setTodayCompletedRecords(todayCompletedRecords);

        List<Map<String, Object>> recentCheckIns = getRecentCheckIns(userId);
        dashboard.setRecentCheckIns(recentCheckIns);

        List<Map<String, Object>> weekStatistics = getWeekStatistics(userId);
        dashboard.setWeekStatistics(weekStatistics);

        return dashboard;
    }

    private List<Map<String, Object>> getRecentCheckIns(Long userId) {
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(6 - i);
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.format(DateTimeFormatter.ofPattern("MM-dd")));
            dayData.put("fullDate", date.toString());

            LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CheckIn::getUserId, userId)
                    .eq(CheckIn::getCheckInDate, date)
                    .eq(CheckIn::getDeleted, 0);
            int count = (int) checkInService.count(wrapper);
            dayData.put("checkInCount", count);
            dayData.put("hasCheckIn", count > 0);

            result.add(dayData);
        }

        return result;
    }

    private List<Map<String, Object>> getWeekStatistics(Long userId) {
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> result = new ArrayList<>();

        String[] types = {"wake_up", "sleep", "exercise", "water", "sleep_early"};
        String[] typeNames = {"起床", "睡觉", "运动", "饮水", "早睡"};

        for (int i = 0; i < types.length; i++) {
            Map<String, Object> typeData = new HashMap<>();
            typeData.put("type", types[i]);
            typeData.put("typeName", typeNames[i]);

            int weekCount = 0;
            for (int j = 0; j < 7; j++) {
                LocalDate date = today.minusDays(j);
                LambdaQueryWrapper<CheckIn> checkInWrapper = new LambdaQueryWrapper<>();
                checkInWrapper.eq(CheckIn::getUserId, userId)
                        .eq(CheckIn::getCheckInDate, date)
                        .eq(CheckIn::getCheckInType, types[i])
                        .eq(CheckIn::getDeleted, 0);
                if (checkInService.count(checkInWrapper) > 0) {
                    weekCount++;
                    continue;
                }

                LambdaQueryWrapper<HealthRecord> recordWrapper = new LambdaQueryWrapper<>();
                recordWrapper.eq(HealthRecord::getUserId, userId)
                        .eq(HealthRecord::getRecordDate, date)
                        .eq(HealthRecord::getRecordType, types[i])
                        .eq(HealthRecord::getStatus, 1)
                        .eq(HealthRecord::getDeleted, 0);
                if (healthRecordService.count(recordWrapper) > 0) {
                    weekCount++;
                }
            }
            typeData.put("weekCount", weekCount);
            typeData.put("completionRate", (double) weekCount / 7 * 100);

            result.add(typeData);
        }

        return result;
    }

    @Override
    public MonthlySummaryVO getMonthlySummary(Long userId, Integer year, Integer month) {
        MonthlySummaryVO summary = new MonthlySummaryVO();
        summary.setYear(year);
        summary.setMonth(month);

        YearMonth yearMonth = YearMonth.of(year, month);
        int totalDays = yearMonth.lengthOfMonth();
        summary.setTotalDays(totalDays);

        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        LambdaQueryWrapper<CheckIn> checkInWrapper = new LambdaQueryWrapper<>();
        checkInWrapper.eq(CheckIn::getUserId, userId)
                .between(CheckIn::getCheckInDate, firstDay, lastDay)
                .eq(CheckIn::getDeleted, 0)
                .groupBy(CheckIn::getCheckInDate);
        int checkInDays = checkInService.listObjs(checkInWrapper).size();
        summary.setCheckInDays(checkInDays);

        List<StreakStat> stats = streakStatService.getByUserId(userId);
        int maxStreak = 0;
        for (StreakStat stat : stats) {
            if (stat.getMaxStreak() > maxStreak) {
                maxStreak = stat.getMaxStreak();
            }
        }
        summary.setMaxStreak(maxStreak);
        summary.setAverageStreak(checkInDays > 0 ? checkInDays / 2 : 0);

        double completionRate = (double) checkInDays / totalDays * 100;
        summary.setCompletionRate((int) completionRate);

        List<Map<String, Object>> dailyDetails = getDailyDetails(userId, firstDay, lastDay);
        summary.setDailyDetails(dailyDetails);

        Map<String, Object> typeStatistics = getTypeStatistics(userId, firstDay, lastDay);
        summary.setTypeStatistics(typeStatistics);

        return summary;
    }

    private List<Map<String, Object>> getDailyDetails(Long userId, LocalDate firstDay, LocalDate lastDay) {
        List<Map<String, Object>> result = new ArrayList<>();

        LocalDate date = firstDay;
        while (!date.isAfter(lastDay)) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("day", date.getDayOfMonth());

            LambdaQueryWrapper<CheckIn> checkInWrapper = new LambdaQueryWrapper<>();
            checkInWrapper.eq(CheckIn::getUserId, userId)
                    .eq(CheckIn::getCheckInDate, date)
                    .eq(CheckIn::getDeleted, 0);
            List<CheckIn> checkIns = checkInService.list(checkInWrapper);
            dayData.put("checkIns", checkIns);

            LambdaQueryWrapper<HealthRecord> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(HealthRecord::getUserId, userId)
                    .eq(HealthRecord::getRecordDate, date)
                    .eq(HealthRecord::getDeleted, 0);
            List<HealthRecord> records = healthRecordService.list(recordWrapper);
            dayData.put("healthRecords", records);

            int completedCount = 0;
            for (HealthRecord record : records) {
                if (record.getStatus() == 1) {
                    completedCount++;
                }
            }
            dayData.put("completedCount", completedCount);
            dayData.put("totalCount", checkIns.size() + completedCount);

            result.add(dayData);
            date = date.plusDays(1);
        }

        return result;
    }

    private Map<String, Object> getTypeStatistics(Long userId, LocalDate firstDay, LocalDate lastDay) {
        Map<String, Object> result = new HashMap<>();

        String[] types = {"wake_up", "sleep", "exercise", "water", "sleep_early"};
        String[] typeNames = {"起床", "睡觉", "运动", "饮水", "早睡"};

        List<Map<String, Object>> typeList = new ArrayList<>();

        for (int i = 0; i < types.length; i++) {
            Map<String, Object> typeData = new HashMap<>();
            typeData.put("type", types[i]);
            typeData.put("typeName", typeNames[i]);

            int count = 0;
            if (types[i].equals("wake_up") || types[i].equals("sleep")) {
                LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(CheckIn::getUserId, userId)
                        .eq(CheckIn::getCheckInType, types[i])
                        .between(CheckIn::getCheckInDate, firstDay, lastDay)
                        .eq(CheckIn::getDeleted, 0);
                count = (int) checkInService.count(wrapper);
            } else {
                LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(HealthRecord::getUserId, userId)
                        .eq(HealthRecord::getRecordType, types[i])
                        .eq(HealthRecord::getStatus, 1)
                        .between(HealthRecord::getRecordDate, firstDay, lastDay)
                        .eq(HealthRecord::getDeleted, 0);
                count = (int) healthRecordService.count(wrapper);
            }

            typeData.put("count", count);
            typeList.add(typeData);
        }

        result.put("types", typeList);
        return result;
    }
}
