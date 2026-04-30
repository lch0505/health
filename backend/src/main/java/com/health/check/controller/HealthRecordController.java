package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.common.constants.StatisticsConstants;
import com.health.check.dto.HealthRecordDTO;
import com.health.check.dto.query.HealthRecordQueryDTO;
import com.health.check.entity.HealthRecord;
import com.health.check.enums.RecordStatus;
import com.health.check.service.HealthRecordService;
import com.health.check.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/health-record")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @PostMapping
    public Result<HealthRecord> addRecord(@Validated @RequestBody HealthRecordDTO recordDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        HealthRecord record = healthRecordService.addRecord(userId, recordDTO);
        return Result.success("添加成功", record);
    }

    @PutMapping("/{id}")
    public Result<HealthRecord> updateRecord(@PathVariable Long id, @Validated @RequestBody HealthRecordDTO recordDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        HealthRecord record = healthRecordService.updateRecord(id, userId, recordDTO);
        return Result.success("更新成功", record);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        healthRecordService.deleteRecord(id, userId);
        return Result.success();
    }

    @GetMapping("/today")
    public Result<Map<String, Object>> getTodayRecords() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<HealthRecord> records = healthRecordService.getTodayRecords(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);

        int totalCompleted = 0;
        for (HealthRecord record : records) {
            if (RecordStatus.COMPLETED.getCode().equals(record.getStatus())) {
                totalCompleted++;
            }
        }
        result.put("totalCompleted", totalCompleted);
        result.put("totalTypes", StatisticsConstants.TOTAL_HEALTH_TYPES);

        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<Page<HealthRecord>> getRecordList(@Validated HealthRecordQueryDTO query) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<HealthRecord> recordPage = healthRecordService.getRecordPage(
                userId, query.getPage(), query.getSize(),
                query.getRecordType(), query.getStartDate(), query.getEndDate());
        return Result.success(recordPage);
    }

    @GetMapping("/{id}")
    public Result<HealthRecord> getRecordDetail(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        HealthRecord record = healthRecordService.getById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            return Result.error(404, "记录不存在");
        }
        return Result.success(record);
    }
}
