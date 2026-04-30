package com.health.check.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.dto.DietRecordDTO;
import com.health.check.dto.query.AdminDietRecordQueryDTO;
import com.health.check.dto.query.DietRecordQueryDTO;
import com.health.check.entity.DietRecord;

import java.time.LocalDate;
import java.util.List;

public interface DietRecordService extends IService<DietRecord> {
    DietRecord addDietRecord(Long userId, DietRecordDTO dietRecordDTO);

    DietRecord updateDietRecord(Long id, Long userId, DietRecordDTO dietRecordDTO);

    void deleteDietRecord(Long id, Long userId);

    Page<DietRecord> getDietRecordPage(DietRecordQueryDTO query);

    Page<DietRecord> getAllDietRecordPage(AdminDietRecordQueryDTO query);

    List<DietRecord> getTodayDietRecords(Long userId);

    DietRecord getDietRecordByDateAndMealType(Long userId, LocalDate date, String mealType);

    List<DietRecord> getDietRecordsByDate(Long userId, LocalDate date);
}
