package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.MoodRecordDTO;
import com.health.check.dto.query.AdminMoodRecordQueryDTO;
import com.health.check.entity.MoodRecord;
import com.health.check.enums.ResponseCode;
import com.health.check.service.MoodRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/mood-record")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMoodRecordController {

    @Autowired
    private MoodRecordService moodRecordService;

    @GetMapping("/list")
    public Result<Page<MoodRecord>> getMoodRecordList(@Validated AdminMoodRecordQueryDTO query) {
        Page<MoodRecord> moodRecordPage = moodRecordService.getAllMoodRecordPage(query);
        return Result.success(moodRecordPage);
    }

    @GetMapping("/{id}")
    public Result<MoodRecord> getMoodRecordDetail(@PathVariable Long id) {
        MoodRecord moodRecord = moodRecordService.getById(id);
        if (moodRecord == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }
        return Result.success(moodRecord);
    }

    @PostMapping
    @Transactional
    public Result<MoodRecord> addMoodRecord(@RequestParam Long userId, @Validated @RequestBody MoodRecordDTO moodRecordDTO) {
        LocalDate today = LocalDate.now();
        MoodRecord existRecord = moodRecordService.getMoodRecordByDate(userId, today);
        if (existRecord != null) {
            return Result.error(ResponseCode.RECORD_EXISTS.getCode(), ResponseCode.RECORD_EXISTS.getMessage());
        }

        MoodRecord moodRecord = new MoodRecord();
        BeanUtils.copyProperties(moodRecordDTO, moodRecord);
        moodRecord.setUserId(userId);
        moodRecord.setRecordDate(today);

        moodRecordService.save(moodRecord);
        return Result.success("添加成功", moodRecord);
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<MoodRecord> updateMoodRecord(@PathVariable Long id, @Validated @RequestBody MoodRecordDTO moodRecordDTO) {
        MoodRecord moodRecord = moodRecordService.getById(id);
        if (moodRecord == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }

        if (moodRecordDTO.getMoodType() != null) {
            moodRecord.setMoodType(moodRecordDTO.getMoodType());
        }
        if (moodRecordDTO.getRemark() != null) {
            moodRecord.setRemark(moodRecordDTO.getRemark());
        }

        moodRecordService.updateById(moodRecord);
        return Result.success("更新成功", moodRecordService.getById(id));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> deleteMoodRecord(@PathVariable Long id) {
        MoodRecord moodRecord = moodRecordService.getById(id);
        if (moodRecord == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }

        moodRecordService.removeById(id);
        return Result.success();
    }
}
