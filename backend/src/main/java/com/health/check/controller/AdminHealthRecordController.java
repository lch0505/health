package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.query.AdminHealthRecordQueryDTO;
import com.health.check.entity.HealthRecord;
import com.health.check.enums.ResponseCode;
import com.health.check.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/health-record")
@PreAuthorize("hasRole('ADMIN')")
public class AdminHealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @GetMapping("/list")
    public Result<Page<HealthRecord>> getRecordList(@Validated AdminHealthRecordQueryDTO query) {
        Page<HealthRecord> recordPage = healthRecordService.getAllRecordPage(query);
        return Result.success(recordPage);
    }

    @GetMapping("/{id}")
    public Result<HealthRecord> getRecordDetail(@PathVariable Long id) {
        HealthRecord record = healthRecordService.getById(id);
        if (record == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }
        return Result.success(record);
    }
}
