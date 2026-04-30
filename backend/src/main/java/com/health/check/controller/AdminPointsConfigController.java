package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.entity.PointsConfig;
import com.health.check.enums.ResponseCode;
import com.health.check.service.PointsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/points-config")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPointsConfigController {

    @Autowired
    private PointsConfigService pointsConfigService;

    @GetMapping("/list")
    public Result<Page<PointsConfig>> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String pointsType,
            @RequestParam(required = false) Integer status) {
        
        Page<PointsConfig> result = pointsConfigService.getAdminPage(page, size, pointsType, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<PointsConfig> getById(@PathVariable Long id) {
        PointsConfig config = pointsConfigService.getById(id);
        if (config == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "积分配置不存在");
        }
        return Result.success(config);
    }

    @PostMapping("/add")
    public Result<PointsConfig> add(@RequestBody PointsConfig config) {
        if (config.getPointsType() == null || config.getPoints() == null) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "积分类型和积分值不能为空");
        }
        
        PointsConfig existing = pointsConfigService.getByPointsType(config.getPointsType());
        if (existing != null) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "该积分类型已存在配置");
        }
        
        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        
        pointsConfigService.save(config);
        return Result.success("配置添加成功", config);
    }

    @PutMapping("/update")
    public Result<PointsConfig> update(@RequestBody PointsConfig config) {
        if (config.getId() == null) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "ID不能为空");
        }
        
        PointsConfig existing = pointsConfigService.getById(config.getId());
        if (existing == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "积分配置不存在");
        }
        
        pointsConfigService.updateById(config);
        return Result.success("配置更新成功", config);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        PointsConfig config = pointsConfigService.getById(id);
        if (config == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "积分配置不存在");
        }
        
        pointsConfigService.removeById(id);
        return Result.success("配置删除成功");
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        PointsConfig config = pointsConfigService.getById(id);
        if (config == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "积分配置不存在");
        }
        
        if (status != 0 && status != 1) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "状态值无效");
        }
        
        config.setStatus(status);
        pointsConfigService.updateById(config);
        return Result.success("状态更新成功");
    }
}
