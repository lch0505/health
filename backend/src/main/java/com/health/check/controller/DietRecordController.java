package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.DietRecordDTO;
import com.health.check.dto.query.DietRecordQueryDTO;
import com.health.check.entity.DietRecord;
import com.health.check.enums.ResponseCode;
import com.health.check.service.DietRecordService;
import com.health.check.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/diet-record")
public class DietRecordController {

    @Autowired
    private DietRecordService dietRecordService;

    @PostMapping
    public Result<DietRecord> addDietRecord(@Validated @RequestBody DietRecordDTO dietRecordDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        DietRecord dietRecord = dietRecordService.addDietRecord(userId, dietRecordDTO);
        return Result.success("添加成功", dietRecord);
    }

    @PutMapping("/{id}")
    public Result<DietRecord> updateDietRecord(@PathVariable Long id, @Validated @RequestBody DietRecordDTO dietRecordDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        DietRecord dietRecord = dietRecordService.updateDietRecord(id, userId, dietRecordDTO);
        return Result.success("更新成功", dietRecord);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDietRecord(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        dietRecordService.deleteDietRecord(id, userId);
        return Result.success();
    }

    @GetMapping("/today")
    public Result<Map<String, Object>> getTodayDietRecords() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<DietRecord> records = dietRecordService.getTodayDietRecords(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("totalRecords", records.size());

        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<Page<DietRecord>> getDietRecordList(@Validated DietRecordQueryDTO query) {
        Long userId = SecurityUtils.getCurrentUserId();
        query.setUserId(userId);
        Page<DietRecord> dietRecordPage = dietRecordService.getDietRecordPage(query);
        return Result.success(dietRecordPage);
    }

    @GetMapping("/{id}")
    public Result<DietRecord> getDietRecordDetail(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        DietRecord dietRecord = dietRecordService.getById(id);
        if (dietRecord == null || !dietRecord.getUserId().equals(userId)) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }
        return Result.success(dietRecord);
    }
}
