package com.health.check.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.entity.PointsConfig;

import java.util.List;

public interface PointsConfigService extends IService<PointsConfig> {
    
    Page<PointsConfig> getAdminPage(Integer page, Integer size, String pointsType, Integer status);
    
    PointsConfig getByPointsType(String pointsType);
    
    List<PointsConfig> getAllEnabled();
    
    Integer getPointsByType(String pointsType);
}
