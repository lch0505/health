package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.entity.PointsConfig;
import com.health.check.enums.PointsType;
import com.health.check.mapper.PointsConfigMapper;
import com.health.check.service.PointsConfigService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PointsConfigServiceImpl extends ServiceImpl<PointsConfigMapper, PointsConfig> implements PointsConfigService {
    
    @Override
    public Page<PointsConfig> getAdminPage(Integer page, Integer size, String pointsType, Integer status) {
        Page<PointsConfig> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PointsConfig> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(pointsType)) {
            wrapper.eq(PointsConfig::getPointsType, pointsType);
        }
        if (status != null) {
            wrapper.eq(PointsConfig::getStatus, status);
        }
        wrapper.orderByAsc(PointsConfig::getId);
        
        return this.page(pageParam, wrapper);
    }
    
    @Override
    public PointsConfig getByPointsType(String pointsType) {
        LambdaQueryWrapper<PointsConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsConfig::getPointsType, pointsType);
        return this.getOne(wrapper);
    }
    
    @Override
    public List<PointsConfig> getAllEnabled() {
        LambdaQueryWrapper<PointsConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsConfig::getStatus, 1);
        wrapper.orderByAsc(PointsConfig::getId);
        return this.list(wrapper);
    }
    
    @Override
    public Integer getPointsByType(String pointsType) {
        PointsConfig config = getByPointsType(pointsType);
        if (config != null && config.getStatus() == 1) {
            return config.getPoints();
        }
        
        PointsType pointsTypeEnum = PointsType.getByCode(pointsType);
        if (pointsTypeEnum != null) {
            return pointsTypeEnum.getDefaultPoints();
        }
        
        return 0;
    }
}
