package com.health.check.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.dto.CheckInDTO;
import com.health.check.dto.query.AdminCheckInQueryDTO;
import com.health.check.dto.query.CheckInQueryDTO;
import com.health.check.entity.CheckIn;

public interface CheckInService extends IService<CheckIn> {
    CheckIn checkIn(Long userId, CheckInDTO checkInDTO);

    CheckIn getTodayCheckIn(Long userId, String checkInType);

    Page<CheckIn> getCheckInPage(CheckInQueryDTO query);

    Page<CheckIn> getAllCheckInPage(AdminCheckInQueryDTO query);

    boolean hasCheckedInToday(Long userId, String checkInType);
}
