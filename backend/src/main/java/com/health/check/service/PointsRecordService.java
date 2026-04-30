package com.health.check.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.entity.PointsRecord;

import java.time.LocalDate;
import java.util.List;

public interface PointsRecordService extends IService<PointsRecord> {
    List<PointsRecord> getByUserId(Long userId);

    Page<PointsRecord> getPageByUserId(Long userId, Integer page, Integer size, LocalDate startDate, LocalDate endDate);

    Page<PointsRecord> getAllPointsRecordPage(Integer page, Integer size, Long userId, LocalDate startDate, LocalDate endDate);
}
