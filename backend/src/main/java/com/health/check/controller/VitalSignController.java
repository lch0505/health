package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.VitalSignDTO;
import com.health.check.dto.query.VitalSignQueryDTO;
import com.health.check.entity.VitalSign;
import com.health.check.enums.ResponseCode;
import com.health.check.service.VitalSignService;
import com.health.check.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/vital-sign")
public class VitalSignController {

    @Autowired
    private VitalSignService vitalSignService;

    @PostMapping
    public Result<VitalSign> addVitalSign(@Validated @RequestBody VitalSignDTO vitalSignDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        VitalSign vitalSign = vitalSignService.addVitalSign(userId, vitalSignDTO);
        return Result.success("添加成功", vitalSign);
    }

    @PutMapping("/{id}")
    public Result<VitalSign> updateVitalSign(@PathVariable Long id, @Validated @RequestBody VitalSignDTO vitalSignDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        VitalSign vitalSign = vitalSignService.updateVitalSign(id, userId, vitalSignDTO);
        return Result.success("更新成功", vitalSign);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteVitalSign(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        vitalSignService.deleteVitalSign(id, userId);
        return Result.success();
    }

    @GetMapping("/today")
    public Result<Map<String, Object>> getTodayVitalSign() {
        Long userId = SecurityUtils.getCurrentUserId();
        VitalSign vitalSign = vitalSignService.getTodayVitalSign(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("vitalSign", vitalSign);
        result.put("hasRecord", vitalSign != null);

        return Result.success(result);
    }

    @GetMapping("/recent/{days}")
    public Result<List<VitalSign>> getRecentVitalSigns(@PathVariable Integer days) {
        if (days == null || days < 1) {
            days = 7;
        }
        if (days > 30) {
            days = 30;
        }
        Long userId = SecurityUtils.getCurrentUserId();
        List<VitalSign> vitalSigns = vitalSignService.getRecentVitalSigns(userId, days);
        return Result.success(vitalSigns);
    }

    @GetMapping("/list")
    public Result<Page<VitalSign>> getVitalSignList(@Validated VitalSignQueryDTO query) {
        Long userId = SecurityUtils.getCurrentUserId();
        query.setUserId(userId);
        Page<VitalSign> vitalSignPage = vitalSignService.getVitalSignPage(query);
        return Result.success(vitalSignPage);
    }

    @GetMapping("/{id}")
    public Result<VitalSign> getVitalSignDetail(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        VitalSign vitalSign = vitalSignService.getById(id);
        if (vitalSign == null || !vitalSign.getUserId().equals(userId)) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }
        return Result.success(vitalSign);
    }
}
