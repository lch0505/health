package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.HealthRecordDTO;
import com.health.check.entity.HealthRecord;
import com.health.check.service.HealthRecordService;
import com.health.check.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/health-record")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @Autowired
    private UserService userService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getByUsername(username).getId();
    }

    @PostMapping
    public Result<HealthRecord> addRecord(@Validated @RequestBody HealthRecordDTO recordDTO) {
        Long userId = getCurrentUserId();
        HealthRecord record = healthRecordService.addRecord(userId, recordDTO);
        return Result.success("添加成功", record);
    }

    @PutMapping("/{id}")
    public Result<HealthRecord> updateRecord(@PathVariable Long id, @Validated @RequestBody HealthRecordDTO recordDTO) {
        Long userId = getCurrentUserId();
        HealthRecord record = healthRecordService.updateRecord(id, userId, recordDTO);
        return Result.success("更新成功", record);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        healthRecordService.deleteRecord(id, userId);
        return Result.success();
    }

    @GetMapping("/today")
    public Result<Map<String, Object>> getTodayRecords() {
        Long userId = getCurrentUserId();
        List<HealthRecord> records = healthRecordService.getTodayRecords(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);

        int totalCompleted = 0;
        for (HealthRecord record : records) {
            if (record.getStatus() == 1) {
                totalCompleted++;
            }
        }
        result.put("totalCompleted", totalCompleted);
        result.put("totalTypes", 3);

        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<Page<HealthRecord>> getRecordList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String recordType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Long userId = getCurrentUserId();
        Page<HealthRecord> recordPage = healthRecordService.getRecordPage(userId, page, size, recordType, startDate, endDate);
        return Result.success(recordPage);
    }

    @GetMapping("/{id}")
    public Result<HealthRecord> getRecordDetail(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        HealthRecord record = healthRecordService.getById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            return Result.error(404, "记录不存在");
        }
        return Result.success(record);
    }
}
