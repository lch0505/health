package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.entity.HealthRecord;
import com.health.check.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/health-record")
@PreAuthorize("hasRole('ADMIN')")
public class AdminHealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @GetMapping("/list")
    public Result<Page<HealthRecord>> getRecordList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String recordType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<HealthRecord> recordPage = healthRecordService.getAllRecordPage(page, size, userId, recordType, startDate, endDate);
        return Result.success(recordPage);
    }

    @GetMapping("/{id}")
    public Result<HealthRecord> getRecordDetail(@PathVariable Long id) {
        HealthRecord record = healthRecordService.getById(id);
        if (record == null) {
            return Result.error(404, "记录不存在");
        }
        return Result.success(record);
    }
}
