package com.health.check.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.dto.CheckInDTO;
import com.health.check.entity.CheckIn;

import java.time.LocalDate;

public interface CheckInService extends IService<CheckIn> {
    CheckIn checkIn(Long userId, CheckInDTO checkInDTO);

    CheckIn getTodayCheckIn(Long userId, String checkInType);

    Page<CheckIn> getCheckInPage(Long userId, Integer page, Integer size, String checkInType, LocalDate startDate, LocalDate endDate);

    Page<CheckIn> getAllCheckInPage(Integer page, Integer size, Long userId, String checkInType, LocalDate startDate, LocalDate endDate);

    boolean hasCheckedInToday(Long userId, String checkInType);
}
