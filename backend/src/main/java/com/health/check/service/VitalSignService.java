package com.health.check.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.dto.VitalSignDTO;
import com.health.check.dto.query.AdminVitalSignQueryDTO;
import com.health.check.dto.query.VitalSignQueryDTO;
import com.health.check.entity.VitalSign;

import java.time.LocalDate;
import java.util.List;

public interface VitalSignService extends IService<VitalSign> {
    VitalSign addVitalSign(Long userId, VitalSignDTO vitalSignDTO);

    VitalSign updateVitalSign(Long id, Long userId, VitalSignDTO vitalSignDTO);

    void deleteVitalSign(Long id, Long userId);

    Page<VitalSign> getVitalSignPage(VitalSignQueryDTO query);

    Page<VitalSign> getAllVitalSignPage(AdminVitalSignQueryDTO query);

    VitalSign getTodayVitalSign(Long userId);

    VitalSign getVitalSignByDate(Long userId, LocalDate date);

    List<VitalSign> getRecentVitalSigns(Long userId, int days);
}
