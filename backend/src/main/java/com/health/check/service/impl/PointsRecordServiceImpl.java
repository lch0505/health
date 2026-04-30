package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.entity.PointsRecord;
import com.health.check.mapper.PointsRecordMapper;
import com.health.check.service.PointsRecordService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord> implements PointsRecordService {

    @Override
    public List<PointsRecord> getByUserId(Long userId) {
        return list(new LambdaQueryWrapper<PointsRecord>()
                .eq(PointsRecord::getUserId, userId)
                .orderByDesc(PointsRecord::getCreateTime));
    }

    @Override
    public Page<PointsRecord> getPageByUserId(Long userId, Integer page, Integer size, LocalDate startDate, LocalDate endDate) {
        Page<PointsRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(PointsRecord::getUserId, userId);

        if (startDate != null) {
            wrapper.ge(PointsRecord::getRecordDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(PointsRecord::getRecordDate, endDate);
        }

        wrapper.orderByDesc(PointsRecord::getRecordDate)
                .orderByDesc(PointsRecord::getCreateTime);

        return page(pageParam, wrapper);
    }

    @Override
    public Page<PointsRecord> getAllPointsRecordPage(Integer page, Integer size, Long userId, LocalDate startDate, LocalDate endDate) {
        Page<PointsRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(PointsRecord::getUserId, userId);
        }
        if (startDate != null) {
            wrapper.ge(PointsRecord::getRecordDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(PointsRecord::getRecordDate, endDate);
        }

        wrapper.orderByDesc(PointsRecord::getRecordDate)
                .orderByDesc(PointsRecord::getCreateTime);

        return page(pageParam, wrapper);
    }
}
