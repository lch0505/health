package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.dto.MoodRecordDTO;
import com.health.check.dto.query.AdminMoodRecordQueryDTO;
import com.health.check.dto.query.MoodRecordQueryDTO;
import com.health.check.entity.MoodRecord;
import com.health.check.enums.DeletedStatus;
import com.health.check.enums.ResponseCode;
import com.health.check.exception.BusinessException;
import com.health.check.mapper.MoodRecordMapper;
import com.health.check.service.MoodRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MoodRecordServiceImpl extends ServiceImpl<MoodRecordMapper, MoodRecord> implements MoodRecordService {

    @Override
    public MoodRecord addMoodRecord(Long userId, MoodRecordDTO moodRecordDTO) {
        LocalDate today = LocalDate.now();

        MoodRecord existRecord = getMoodRecordByDate(userId, today);
        if (existRecord != null) {
            throw new BusinessException(ResponseCode.RECORD_EXISTS);
        }

        MoodRecord moodRecord = new MoodRecord();
        BeanUtils.copyProperties(moodRecordDTO, moodRecord);
        moodRecord.setUserId(userId);
        moodRecord.setRecordDate(today);

        save(moodRecord);
        return moodRecord;
    }

    @Override
    public MoodRecord updateMoodRecord(Long id, Long userId, MoodRecordDTO moodRecordDTO) {
        MoodRecord moodRecord = getById(id);
        if (moodRecord == null) {
            throw new BusinessException(ResponseCode.RECORD_NOT_FOUND);
        }

        if (!moodRecord.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NO_PERMISSION);
        }

        if (moodRecordDTO.getMoodType() != null) {
            moodRecord.setMoodType(moodRecordDTO.getMoodType());
        }
        if (moodRecordDTO.getRemark() != null) {
            moodRecord.setRemark(moodRecordDTO.getRemark());
        }

        updateById(moodRecord);
        return getById(id);
    }

    @Override
    public void deleteMoodRecord(Long id, Long userId) {
        MoodRecord moodRecord = getById(id);
        if (moodRecord == null) {
            throw new BusinessException(ResponseCode.RECORD_NOT_FOUND);
        }

        if (!moodRecord.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NO_PERMISSION);
        }

        removeById(id);
    }

    @Override
    public Page<MoodRecord> getMoodRecordPage(MoodRecordQueryDTO query) {
        Page<MoodRecord> pageParam = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<MoodRecord> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(MoodRecord::getUserId, query.getUserId());

        if (query.getMoodType() != null && !query.getMoodType().isEmpty()) {
            wrapper.eq(MoodRecord::getMoodType, query.getMoodType());
        }

        if (query.getStartDate() != null) {
            wrapper.ge(MoodRecord::getRecordDate, query.getStartDate());
        }

        if (query.getEndDate() != null) {
            wrapper.le(MoodRecord::getRecordDate, query.getEndDate());
        }

        wrapper.eq(MoodRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .orderByDesc(MoodRecord::getRecordDate);

        return page(pageParam, wrapper);
    }

    @Override
    public Page<MoodRecord> getAllMoodRecordPage(AdminMoodRecordQueryDTO query) {
        Page<MoodRecord> pageParam = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<MoodRecord> wrapper = new LambdaQueryWrapper<>();

        if (query.getUserId() != null) {
            wrapper.eq(MoodRecord::getUserId, query.getUserId());
        }

        if (query.getMoodType() != null && !query.getMoodType().isEmpty()) {
            wrapper.eq(MoodRecord::getMoodType, query.getMoodType());
        }

        if (query.getStartDate() != null) {
            wrapper.ge(MoodRecord::getRecordDate, query.getStartDate());
        }

        if (query.getEndDate() != null) {
            wrapper.le(MoodRecord::getRecordDate, query.getEndDate());
        }

        wrapper.eq(MoodRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .orderByDesc(MoodRecord::getRecordDate);

        return page(pageParam, wrapper);
    }

    @Override
    public MoodRecord getTodayMoodRecord(Long userId) {
        return getMoodRecordByDate(userId, LocalDate.now());
    }

    @Override
    public MoodRecord getMoodRecordByDate(Long userId, LocalDate date) {
        return getOne(new LambdaQueryWrapper<MoodRecord>()
                .eq(MoodRecord::getUserId, userId)
                .eq(MoodRecord::getRecordDate, date)
                .eq(MoodRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .last("LIMIT 1"));
    }

    @Override
    public List<MoodRecord> getRecentMoodRecords(Long userId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        return list(new LambdaQueryWrapper<MoodRecord>()
                .eq(MoodRecord::getUserId, userId)
                .between(MoodRecord::getRecordDate, startDate, endDate)
                .eq(MoodRecord::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .orderByDesc(MoodRecord::getRecordDate));
    }
}
