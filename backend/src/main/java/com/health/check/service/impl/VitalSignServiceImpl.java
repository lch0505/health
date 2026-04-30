package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.dto.VitalSignDTO;
import com.health.check.dto.query.AdminVitalSignQueryDTO;
import com.health.check.dto.query.VitalSignQueryDTO;
import com.health.check.entity.VitalSign;
import com.health.check.enums.DeletedStatus;
import com.health.check.enums.ResponseCode;
import com.health.check.exception.BusinessException;
import com.health.check.mapper.VitalSignMapper;
import com.health.check.service.VitalSignService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VitalSignServiceImpl extends ServiceImpl<VitalSignMapper, VitalSign> implements VitalSignService {

    @Override
    public VitalSign addVitalSign(Long userId, VitalSignDTO vitalSignDTO) {
        LocalDate today = LocalDate.now();

        VitalSign existRecord = getVitalSignByDate(userId, today);
        if (existRecord != null) {
            throw new BusinessException(ResponseCode.RECORD_EXISTS);
        }

        VitalSign vitalSign = new VitalSign();
        BeanUtils.copyProperties(vitalSignDTO, vitalSign);
        vitalSign.setUserId(userId);
        vitalSign.setRecordDate(today);

        save(vitalSign);
        return vitalSign;
    }

    @Override
    public VitalSign updateVitalSign(Long id, Long userId, VitalSignDTO vitalSignDTO) {
        VitalSign vitalSign = getById(id);
        if (vitalSign == null) {
            throw new BusinessException(ResponseCode.RECORD_NOT_FOUND);
        }

        if (!vitalSign.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NO_PERMISSION);
        }

        if (vitalSignDTO.getWeight() != null) {
            vitalSign.setWeight(vitalSignDTO.getWeight());
        }
        if (vitalSignDTO.getBodyFat() != null) {
            vitalSign.setBodyFat(vitalSignDTO.getBodyFat());
        }
        if (vitalSignDTO.getSystolicPressure() != null) {
            vitalSign.setSystolicPressure(vitalSignDTO.getSystolicPressure());
        }
        if (vitalSignDTO.getDiastolicPressure() != null) {
            vitalSign.setDiastolicPressure(vitalSignDTO.getDiastolicPressure());
        }
        if (vitalSignDTO.getVisionStatus() != null) {
            vitalSign.setVisionStatus(vitalSignDTO.getVisionStatus());
        }
        if (vitalSignDTO.getSleepQuality() != null) {
            vitalSign.setSleepQuality(vitalSignDTO.getSleepQuality());
        }
        if (vitalSignDTO.getRemark() != null) {
            vitalSign.setRemark(vitalSignDTO.getRemark());
        }

        updateById(vitalSign);
        return getById(id);
    }

    @Override
    public void deleteVitalSign(Long id, Long userId) {
        VitalSign vitalSign = getById(id);
        if (vitalSign == null) {
            throw new BusinessException(ResponseCode.RECORD_NOT_FOUND);
        }

        if (!vitalSign.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NO_PERMISSION);
        }

        removeById(id);
    }

    @Override
    public Page<VitalSign> getVitalSignPage(VitalSignQueryDTO query) {
        Page<VitalSign> pageParam = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<VitalSign> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(VitalSign::getUserId, query.getUserId());

        if (query.getVisionStatus() != null && !query.getVisionStatus().isEmpty()) {
            wrapper.eq(VitalSign::getVisionStatus, query.getVisionStatus());
        }

        if (query.getSleepQuality() != null) {
            wrapper.eq(VitalSign::getSleepQuality, query.getSleepQuality());
        }

        if (query.getStartDate() != null) {
            wrapper.ge(VitalSign::getRecordDate, query.getStartDate());
        }

        if (query.getEndDate() != null) {
            wrapper.le(VitalSign::getRecordDate, query.getEndDate());
        }

        wrapper.eq(VitalSign::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .orderByDesc(VitalSign::getRecordDate);

        return page(pageParam, wrapper);
    }

    @Override
    public Page<VitalSign> getAllVitalSignPage(AdminVitalSignQueryDTO query) {
        Page<VitalSign> pageParam = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<VitalSign> wrapper = new LambdaQueryWrapper<>();

        if (query.getUserId() != null) {
            wrapper.eq(VitalSign::getUserId, query.getUserId());
        }

        if (query.getVisionStatus() != null && !query.getVisionStatus().isEmpty()) {
            wrapper.eq(VitalSign::getVisionStatus, query.getVisionStatus());
        }

        if (query.getSleepQuality() != null) {
            wrapper.eq(VitalSign::getSleepQuality, query.getSleepQuality());
        }

        if (query.getStartDate() != null) {
            wrapper.ge(VitalSign::getRecordDate, query.getStartDate());
        }

        if (query.getEndDate() != null) {
            wrapper.le(VitalSign::getRecordDate, query.getEndDate());
        }

        wrapper.eq(VitalSign::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .orderByDesc(VitalSign::getRecordDate);

        return page(pageParam, wrapper);
    }

    @Override
    public VitalSign getTodayVitalSign(Long userId) {
        return getVitalSignByDate(userId, LocalDate.now());
    }

    @Override
    public VitalSign getVitalSignByDate(Long userId, LocalDate date) {
        return getOne(new LambdaQueryWrapper<VitalSign>()
                .eq(VitalSign::getUserId, userId)
                .eq(VitalSign::getRecordDate, date)
                .eq(VitalSign::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .last("LIMIT 1"));
    }

    @Override
    public List<VitalSign> getRecentVitalSigns(Long userId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        return list(new LambdaQueryWrapper<VitalSign>()
                .eq(VitalSign::getUserId, userId)
                .between(VitalSign::getRecordDate, startDate, endDate)
                .eq(VitalSign::getDeleted, DeletedStatus.NOT_DELETED.getCode())
                .orderByDesc(VitalSign::getRecordDate));
    }
}
