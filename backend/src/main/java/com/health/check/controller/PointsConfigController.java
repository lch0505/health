package com.health.check.controller;

import com.health.check.common.Result;
import com.health.check.entity.PointsConfig;
import com.health.check.service.PointsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points-config")
public class PointsConfigController {

    @Autowired
    private PointsConfigService pointsConfigService;

    @GetMapping("/list")
    public Result<List<PointsConfig>> getList() {
        List<PointsConfig> configs = pointsConfigService.getAllEnabled();
        return Result.success(configs);
    }

    @GetMapping("/{pointsType}")
    public Result<PointsConfig> getByType(@PathVariable String pointsType) {
        PointsConfig config = pointsConfigService.getByPointsType(pointsType);
        if (config == null || config.getStatus() != 1) {
            return Result.error(404, "积分配置不存在或已禁用");
        }
        return Result.success(config);
    }
}
