package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.dto.CheckInDTO;
import com.health.check.entity.CheckIn;
import com.health.check.entity.StreakStat;
import com.health.check.enums.DeletedStatus;
import com.health.check.enums.RecordStatus;
import com.health.check.enums.ResponseCode;
import com.health.check.exception.BusinessException;
import com.health.check.mapper.CheckInMapper;
import com.health.check.service.CheckInService;
import com.health.check.service.StreakStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements CheckInService {

    @Autowired
    private StreakStatService streakStatService;

    @Override
    @Transactional
    public CheckIn checkIn(Long userId, CheckInDTO checkInDTO) {
        LocalDate today = LocalDate.now();

        if (hasCheckedInToday(userId, checkInDTO.getCheckInType())) {
            throw new BusinessException(ResponseCode.CHECK_IN_EXISTS);
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setUserId(userId);
        checkIn.setCheckInDate(today);
        checkIn.setCheckInTime(LocalDateTime.now());
        checkIn.setCheckInType(checkInDTO.getCheckInType());
        checkIn.setStatus(RecordStatus.COMPLETED.getCode());
        checkIn.setRemark(checkInDTO.getRemark());

        save(checkIn);

        updateStreak(userId, checkInDTO.getCheckInType(), today);

        return checkIn;
    }

    private void updateStreak(Long userId, String checkInType, LocalDate today) {
        StreakStat streakStat = streakStatService.getByUserIdAndType(userId, checkInType);

        if (streakStat == null) {
            streakStat = new StreakStat();
            streakStat.setUserId(userId);
            streakStat.setStreakType(checkInType);
            streakStat.setCurrentStreak(1);
            streakStat.setMaxStreak(1);
            streakStat.setLastCheckInDate(today);
            streakStatService.save(streakStat);
        } else {
            LocalDate lastDate = streakStat.getLastCheckInDate();
            if (lastDate == null) {
                streakStat.setCurrentStreak(1);
            } else if (lastDate.equals(today.minusDays(1))) {
                streakStat.setCurrentStreak(streakStat.getCurrentStreak() + 1);
            } else if (lastDate.equals(today)) {
            } else {
                streakStat.setCurrentStreak(1);
            }

            streakStat.setLastCheckInDate(today);

            if (streakStat.getCurrentStreak() > streakStat.getMaxStreak()) {
                streakStat.setMaxStreak(streakStat.getCurrentStreak());
            }

            streakStatService.updateById(streakStat);
        }
    }

    @Override
    public CheckIn getTodayCheckIn(Long userId, String checkInType) {
        LocalDate today = LocalDate.now();
        return getOne(new LambdaQueryWrapper<CheckIn>()
                .eq(CheckIn::getUserId, userId)
                .eq(CheckIn::getCheckInDate, today)
                .eq(CheckIn::getCheckInType, checkInType)
                .eq(CheckIn::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .last("LIMIT 1"));
    }

    @Override
    public Page<CheckIn> getCheckInPage(Long userId, Integer page, Integer size, String checkInType, LocalDate startDate, LocalDate endDate) {
        Page<CheckIn> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(CheckIn::getUserId, userId);

        if (checkInType != null && !checkInType.isEmpty()) {
            wrapper.eq(CheckIn::getCheckInType, checkInType);
        }

        if (startDate != null) {
            wrapper.ge(CheckIn::getCheckInDate, startDate);
        }

        if (endDate != null) {
            wrapper.le(CheckIn::getCheckInDate, endDate);
        }

        wrapper.eq(CheckIn::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .orderByDesc(CheckIn::getCheckInDate)
                .orderByDesc(CheckIn::getCheckInTime);

        return page(pageParam, wrapper);
    }

    @Override
    public Page<CheckIn> getAllCheckInPage(Integer page, Integer size, Long userId, String checkInType, LocalDate startDate, LocalDate endDate) {
        Page<CheckIn> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(CheckIn::getUserId, userId);
        }

        if (checkInType != null && !checkInType.isEmpty()) {
            wrapper.eq(CheckIn::getCheckInType, checkInType);
        }

        if (startDate != null) {
            wrapper.ge(CheckIn::getCheckInDate, startDate);
        }

        if (endDate != null) {
            wrapper.le(CheckIn::getCheckInDate, endDate);
        }

        wrapper.eq(CheckIn::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .orderByDesc(CheckIn::getCheckInDate)
                .orderByDesc(CheckIn::getCheckInTime);

        return page(pageParam, wrapper);
    }

    @Override
    public boolean hasCheckedInToday(Long userId, String checkInType) {
        CheckIn todayCheckIn = getTodayCheckIn(userId, checkInType);
        return todayCheckIn != null;
    }
}
