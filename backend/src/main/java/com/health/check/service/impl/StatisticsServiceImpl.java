package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.health.check.common.constants.StatisticsConstants;
import com.health.check.entity.CheckIn;
import com.health.check.entity.HealthRecord;
import com.health.check.entity.StreakStat;
import com.health.check.enums.DeletedStatus;
import com.health.check.enums.RecordStatus;
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
                .eq(CheckIn::getDeleted, DeletedStatus.NOT_DELETED.getCode());
        int todayCheckInCount = (int) checkInService.count(checkInWrapper);
        dashboard.setTodayCheckInCount(todayCheckInCount);

        LambdaQueryWrapper<HealthRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(HealthRecord::getUserId, userId)
                .eq(HealthRecord::getRecordDate, today)
                .eq(HealthRecord::getStatus, RecordStatus.COMPLETED.getCode())
                .eq(HealthRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode());
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

        for (int i = 0; i < StatisticsConstants.WEEK_DAYS; i++) {
            LocalDate date = today.minusDays(StatisticsConstants.WEEK_DAYS - 1 - i);
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.format(DateTimeFormatter.ofPattern("MM-dd")));
            dayData.put("fullDate", date.toString());

            LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CheckIn::getUserId, userId)
                    .eq(CheckIn::getCheckInDate, date)
                    .eq(CheckIn::getDeleted, DeletedStatus.NOT_DELETED.getCode());
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

        String[] types = StatisticsConstants.ALL_TYPES;
        String[] typeNames = StatisticsConstants.ALL_TYPE_NAMES;

        for (int i = 0; i < types.length; i++) {
            Map<String, Object> typeData = new HashMap<>();
            typeData.put("type", types[i]);
            typeData.put("typeName", typeNames[i]);

            int weekCount = 0;
            for (int j = 0; j < StatisticsConstants.WEEK_DAYS; j++) {
                LocalDate date = today.minusDays(j);
                LambdaQueryWrapper<CheckIn> checkInWrapper = new LambdaQueryWrapper<>();
                checkInWrapper.eq(CheckIn::getUserId, userId)
                        .eq(CheckIn::getCheckInDate, date)
                        .eq(CheckIn::getCheckInType, types[i])
                        .eq(CheckIn::getDeleted, DeletedStatus.NOT_DELETED.getCode());
                if (checkInService.count(checkInWrapper) > 0) {
                    weekCount++;
                    continue;
                }

                LambdaQueryWrapper<HealthRecord> recordWrapper = new LambdaQueryWrapper<>();
                recordWrapper.eq(HealthRecord::getUserId, userId)
                        .eq(HealthRecord::getRecordDate, date)
                        .eq(HealthRecord::getRecordType, types[i])
                        .eq(HealthRecord::getStatus, RecordStatus.COMPLETED.getCode())
                        .eq(HealthRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode());
                if (healthRecordService.count(recordWrapper) > 0) {
                    weekCount++;
                }
            }
            typeData.put("weekCount", weekCount);
            typeData.put("completionRate", (double) weekCount / StatisticsConstants.WEEK_DAYS * 100);

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

        int checkInDays = countDistinctCheckInDates(userId, firstDay, lastDay);
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

    private int countDistinctCheckInDates(Long userId, LocalDate firstDay, LocalDate lastDay) {
        LambdaQueryWrapper<CheckIn> checkInWrapper = new LambdaQueryWrapper<>();
        checkInWrapper.eq(CheckIn::getUserId, userId)
                .between(CheckIn::getCheckInDate, firstDay, lastDay)
                .eq(CheckIn::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .orderByAsc(CheckIn::getCheckInDate);

        List<CheckIn> checkIns = checkInService.list(checkInWrapper);
        
        return (int) checkIns.stream()
                .map(CheckIn::getCheckInDate)
                .distinct()
                .count();
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
                    .eq(CheckIn::getDeleted, DeletedStatus.NOT_DELETED.getCode());
            List<CheckIn> checkIns = checkInService.list(checkInWrapper);
            dayData.put("checkIns", checkIns);

            LambdaQueryWrapper<HealthRecord> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(HealthRecord::getUserId, userId)
                    .eq(HealthRecord::getRecordDate, date)
                    .eq(HealthRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode());
            List<HealthRecord> records = healthRecordService.list(recordWrapper);
            dayData.put("healthRecords", records);

            int completedCount = 0;
            for (HealthRecord record : records) {
                if (RecordStatus.COMPLETED.getCode().equals(record.getStatus())) {
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

        String[] types = StatisticsConstants.ALL_TYPES;
        String[] typeNames = StatisticsConstants.ALL_TYPE_NAMES;

        List<Map<String, Object>> typeList = new ArrayList<>();

        for (int i = 0; i < types.length; i++) {
            Map<String, Object> typeData = new HashMap<>();
            typeData.put("type", types[i]);
            typeData.put("typeName", typeNames[i]);

            int count = 0;
            if (isCheckInType(types[i])) {
                LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(CheckIn::getUserId, userId)
                        .eq(CheckIn::getCheckInType, types[i])
                        .between(CheckIn::getCheckInDate, firstDay, lastDay)
                        .eq(CheckIn::getDeleted, DeletedStatus.NOT_DELETED.getCode());
                count = (int) checkInService.count(wrapper);
            } else {
                LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(HealthRecord::getUserId, userId)
                        .eq(HealthRecord::getRecordType, types[i])
                        .eq(HealthRecord::getStatus, RecordStatus.COMPLETED.getCode())
                        .between(HealthRecord::getRecordDate, firstDay, lastDay)
                        .eq(HealthRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode());
                count = (int) healthRecordService.count(wrapper);
            }

            typeData.put("count", count);
            typeList.add(typeData);
        }

        result.put("types", typeList);
        return result;
    }

    private boolean isCheckInType(String type) {
        for (String checkInType : StatisticsConstants.CHECK_IN_TYPES) {
            if (checkInType.equals(type)) {
                return true;
            }
        }
        return false;
    }
}
