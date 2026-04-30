package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.DietRecordDTO;
import com.health.check.dto.query.AdminDietRecordQueryDTO;
import com.health.check.entity.DietRecord;
import com.health.check.enums.ResponseCode;
import com.health.check.service.DietRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/diet-record")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDietRecordController {

    @Autowired
    private DietRecordService dietRecordService;

    @GetMapping("/list")
    public Result<Page<DietRecord>> getDietRecordList(@Validated AdminDietRecordQueryDTO query) {
        Page<DietRecord> dietRecordPage = dietRecordService.getAllDietRecordPage(query);
        return Result.success(dietRecordPage);
    }

    @GetMapping("/{id}")
    public Result<DietRecord> getDietRecordDetail(@PathVariable Long id) {
        DietRecord dietRecord = dietRecordService.getById(id);
        if (dietRecord == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }
        return Result.success(dietRecord);
    }

    @PostMapping
    @Transactional
    public Result<DietRecord> addDietRecord(@RequestParam Long userId, @Validated @RequestBody DietRecordDTO dietRecordDTO) {
        LocalDate today = LocalDate.now();
        DietRecord existRecord = dietRecordService.getDietRecordByDateAndMealType(userId, today, dietRecordDTO.getMealType());
        if (existRecord != null) {
            return Result.error(ResponseCode.RECORD_EXISTS.getCode(), ResponseCode.RECORD_EXISTS.getMessage());
        }

        DietRecord dietRecord = new DietRecord();
        BeanUtils.copyProperties(dietRecordDTO, dietRecord);
        dietRecord.setUserId(userId);
        dietRecord.setRecordDate(today);

        dietRecordService.save(dietRecord);
        return Result.success("添加成功", dietRecord);
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<DietRecord> updateDietRecord(@PathVariable Long id, @Validated @RequestBody DietRecordDTO dietRecordDTO) {
        DietRecord dietRecord = dietRecordService.getById(id);
        if (dietRecord == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }

        if (dietRecordDTO.getMealType() != null) {
            dietRecord.setMealType(dietRecordDTO.getMealType());
        }
        if (dietRecordDTO.getDietTaste() != null) {
            dietRecord.setDietTaste(dietRecordDTO.getDietTaste());
        }
        if (dietRecordDTO.getAvoidFoodCompliance() != null) {
            dietRecord.setAvoidFoodCompliance(dietRecordDTO.getAvoidFoodCompliance());
        }
        if (dietRecordDTO.getStatus() != null) {
            dietRecord.setStatus(dietRecordDTO.getStatus());
        }
        if (dietRecordDTO.getRemark() != null) {
            dietRecord.setRemark(dietRecordDTO.getRemark());
        }

        dietRecordService.updateById(dietRecord);
        return Result.success("更新成功", dietRecordService.getById(id));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> deleteDietRecord(@PathVariable Long id) {
        DietRecord dietRecord = dietRecordService.getById(id);
        if (dietRecord == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }

        dietRecordService.removeById(id);
        return Result.success();
    }
}
