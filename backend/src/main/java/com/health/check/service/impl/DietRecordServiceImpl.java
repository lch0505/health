package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.dto.DietRecordDTO;
import com.health.check.dto.query.AdminDietRecordQueryDTO;
import com.health.check.dto.query.DietRecordQueryDTO;
import com.health.check.entity.DietRecord;
import com.health.check.enums.DeletedStatus;
import com.health.check.enums.ResponseCode;
import com.health.check.exception.BusinessException;
import com.health.check.mapper.DietRecordMapper;
import com.health.check.service.DietRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DietRecordServiceImpl extends ServiceImpl<DietRecordMapper, DietRecord> implements DietRecordService {

    @Override
    public DietRecord addDietRecord(Long userId, DietRecordDTO dietRecordDTO) {
        LocalDate today = LocalDate.now();

        DietRecord existRecord = getDietRecordByDateAndMealType(userId, today, dietRecordDTO.getMealType());
        if (existRecord != null) {
            throw new BusinessException(ResponseCode.RECORD_EXISTS);
        }

        DietRecord dietRecord = new DietRecord();
        BeanUtils.copyProperties(dietRecordDTO, dietRecord);
        dietRecord.setUserId(userId);
        dietRecord.setRecordDate(today);

        save(dietRecord);
        return dietRecord;
    }

    @Override
    public DietRecord updateDietRecord(Long id, Long userId, DietRecordDTO dietRecordDTO) {
        DietRecord dietRecord = getById(id);
        if (dietRecord == null) {
            throw new BusinessException(ResponseCode.RECORD_NOT_FOUND);
        }

        if (!dietRecord.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NO_PERMISSION);
        }

        if (dietRecordDTO.getMealType() != null) {
            dietRecord.setMealType(dietRecordDTO.getMealType());
        }
        if (dietRecordDTO.getDietTaste() != null) {
            dietRecord.setDietTaste(dietRecordDTO.getDietTaste());
        }
        if (dietRecordDTO.getAvoidFoodCompliance() != null) {
            dietRecord.setAvoidFoodCompliance(dietRecordDTO.getAvoidFoodCompliance());
        }
        if (dietRecordDTO.getStatus() != null) {
            dietRecord.setStatus(dietRecordDTO.getStatus());
        }
        if (dietRecordDTO.getRemark() != null) {
            dietRecord.setRemark(dietRecordDTO.getRemark());
        }

        updateById(dietRecord);
        return getById(id);
    }

    @Override
    public void deleteDietRecord(Long id, Long userId) {
        DietRecord dietRecord = getById(id);
        if (dietRecord == null) {
            throw new BusinessException(ResponseCode.RECORD_NOT_FOUND);
        }

        if (!dietRecord.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NO_PERMISSION);
        }

        removeById(id);
    }

    @Override
    public Page<DietRecord> getDietRecordPage(DietRecordQueryDTO query) {
        Page<DietRecord> pageParam = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<DietRecord> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(DietRecord::getUserId, query.getUserId());

        if (query.getMealType() != null && !query.getMealType().isEmpty()) {
            wrapper.eq(DietRecord::getMealType, query.getMealType());
        }

        if (query.getDietTaste() != null && !query.getDietTaste().isEmpty()) {
            wrapper.eq(DietRecord::getDietTaste, query.getDietTaste());
        }

        if (query.getStatus() != null) {
            wrapper.eq(DietRecord::getStatus, query.getStatus());
        }

        if (query.getStartDate() != null) {
            wrapper.ge(DietRecord::getRecordDate, query.getStartDate());
        }

        if (query.getEndDate() != null) {
            wrapper.le(DietRecord::getRecordDate, query.getEndDate());
        }

        wrapper.eq(DietRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .orderByDesc(DietRecord::getRecordDate);

        return page(pageParam, wrapper);
    }

    @Override
    public Page<DietRecord> getAllDietRecordPage(AdminDietRecordQueryDTO query) {
        Page<DietRecord> pageParam = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<DietRecord> wrapper = new LambdaQueryWrapper<>();

        if (query.getUserId() != null) {
            wrapper.eq(DietRecord::getUserId, query.getUserId());
        }

        if (query.getMealType() != null && !query.getMealType().isEmpty()) {
            wrapper.eq(DietRecord::getMealType, query.getMealType());
        }

        if (query.getDietTaste() != null && !query.getDietTaste().isEmpty()) {
            wrapper.eq(DietRecord::getDietTaste, query.getDietTaste());
        }

        if (query.getStatus() != null) {
            wrapper.eq(DietRecord::getStatus, query.getStatus());
        }

        if (query.getStartDate() != null) {
            wrapper.ge(DietRecord::getRecordDate, query.getStartDate());
        }

        if (query.getEndDate() != null) {
            wrapper.le(DietRecord::getRecordDate, query.getEndDate());
        }

        wrapper.eq(DietRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .orderByDesc(DietRecord::getRecordDate);

        return page(pageParam, wrapper);
    }

    @Override
    public List<DietRecord> getTodayDietRecords(Long userId) {
        return getDietRecordsByDate(userId, LocalDate.now());
    }

    @Override
    public DietRecord getDietRecordByDateAndMealType(Long userId, LocalDate date, String mealType) {
        return getOne(new LambdaQueryWrapper<DietRecord>()
                .eq(DietRecord::getUserId, userId)
                .eq(DietRecord::getRecordDate, date)
                .eq(DietRecord::getMealType, mealType)
                .eq(DietRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .last("LIMIT 1"));
    }

    @Override
    public List<DietRecord> getDietRecordsByDate(Long userId, LocalDate date) {
        return list(new LambdaQueryWrapper<DietRecord>()
                .eq(DietRecord::getUserId, userId)
                .eq(DietRecord::getRecordDate, date)
                .eq(DietRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode()));
    }
}
