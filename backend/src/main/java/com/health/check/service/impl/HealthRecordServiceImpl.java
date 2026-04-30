package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.dto.HealthRecordDTO;
import com.health.check.entity.HealthRecord;
import com.health.check.enums.DeletedStatus;
import com.health.check.enums.ResponseCode;
import com.health.check.exception.BusinessException;
import com.health.check.mapper.HealthRecordMapper;
import com.health.check.service.HealthRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {

    @Override
    public HealthRecord addRecord(Long userId, HealthRecordDTO recordDTO) {
        LocalDate today = LocalDate.now();

        HealthRecord existRecord = getRecordByDateAndType(userId, today, recordDTO.getRecordType());
        if (existRecord != null) {
            throw new BusinessException(ResponseCode.RECORD_EXISTS);
        }

        HealthRecord record = new HealthRecord();
        record.setUserId(userId);
        record.setRecordDate(today);
        record.setRecordType(recordDTO.getRecordType());
        record.setQuantity(recordDTO.getQuantity());
        record.setDuration(recordDTO.getDuration());
        record.setStatus(recordDTO.getStatus());
        record.setRemark(recordDTO.getRemark());

        save(record);
        return record;
    }

    @Override
    public HealthRecord updateRecord(Long id, Long userId, HealthRecordDTO recordDTO) {
        HealthRecord record = getById(id);
        if (record == null) {
            throw new BusinessException(ResponseCode.RECORD_NOT_FOUND);
        }

        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NO_PERMISSION);
        }

        record.setQuantity(recordDTO.getQuantity());
        record.setDuration(recordDTO.getDuration());
        record.setStatus(recordDTO.getStatus());
        record.setRemark(recordDTO.getRemark());

        updateById(record);
        return getById(id);
    }

    @Override
    public void deleteRecord(Long id, Long userId) {
        HealthRecord record = getById(id);
        if (record == null) {
            throw new BusinessException(ResponseCode.RECORD_NOT_FOUND);
        }

        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NO_PERMISSION);
        }

        removeById(id);
    }

    @Override
    public Page<HealthRecord> getRecordPage(Long userId, Integer page, Integer size, String recordType, LocalDate startDate, LocalDate endDate) {
        Page<HealthRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(HealthRecord::getUserId, userId);

        if (recordType != null && !recordType.isEmpty()) {
            wrapper.eq(HealthRecord::getRecordType, recordType);
        }

        if (startDate != null) {
            wrapper.ge(HealthRecord::getRecordDate, startDate);
        }

        if (endDate != null) {
            wrapper.le(HealthRecord::getRecordDate, endDate);
        }

        wrapper.eq(HealthRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .orderByDesc(HealthRecord::getRecordDate);

        return page(pageParam, wrapper);
    }

    @Override
    public Page<HealthRecord> getAllRecordPage(Integer page, Integer size, Long userId, String recordType, LocalDate startDate, LocalDate endDate) {
        Page<HealthRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(HealthRecord::getUserId, userId);
        }

        if (recordType != null && !recordType.isEmpty()) {
            wrapper.eq(HealthRecord::getRecordType, recordType);
        }

        if (startDate != null) {
            wrapper.ge(HealthRecord::getRecordDate, startDate);
        }

        if (endDate != null) {
            wrapper.le(HealthRecord::getRecordDate, endDate);
        }

        wrapper.eq(HealthRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .orderByDesc(HealthRecord::getRecordDate);

        return page(pageParam, wrapper);
    }

    @Override
    public List<HealthRecord> getTodayRecords(Long userId) {
        LocalDate today = LocalDate.now();
        return list(new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getUserId, userId)
                .eq(HealthRecord::getRecordDate, today)
                .eq(HealthRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode()));
    }

    @Override
    public HealthRecord getRecordByDateAndType(Long userId, LocalDate date, String recordType) {
        return getOne(new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getUserId, userId)
                .eq(HealthRecord::getRecordDate, date)
                .eq(HealthRecord::getRecordType, recordType)
                .eq(HealthRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .last("LIMIT 1"));
    }
}
