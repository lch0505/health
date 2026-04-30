package com.health.check.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.dto.HealthRecordDTO;
import com.health.check.entity.HealthRecord;

import java.time.LocalDate;
import java.util.List;

public interface HealthRecordService extends IService<HealthRecord> {
    HealthRecord addRecord(Long userId, HealthRecordDTO recordDTO);

    HealthRecord updateRecord(Long id, Long userId, HealthRecordDTO recordDTO);

    void deleteRecord(Long id, Long userId);

    Page<HealthRecord> getRecordPage(Long userId, Integer page, Integer size, String recordType, LocalDate startDate, LocalDate endDate);

    Page<HealthRecord> getAllRecordPage(Integer page, Integer size, Long userId, String recordType, LocalDate startDate, LocalDate endDate);

    List<HealthRecord> getTodayRecords(Long userId);

    HealthRecord getRecordByDateAndType(Long userId, LocalDate date, String recordType);
}
