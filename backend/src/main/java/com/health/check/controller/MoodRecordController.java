package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.MoodRecordDTO;
import com.health.check.dto.query.MoodRecordQueryDTO;
import com.health.check.entity.MoodRecord;
import com.health.check.enums.ResponseCode;
import com.health.check.service.MoodRecordService;
import com.health.check.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/mood-record")
public class MoodRecordController {

    @Autowired
    private MoodRecordService moodRecordService;

    @PostMapping
    public Result<MoodRecord> addMoodRecord(@Validated @RequestBody MoodRecordDTO moodRecordDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        MoodRecord moodRecord = moodRecordService.addMoodRecord(userId, moodRecordDTO);
        return Result.success("添加成功", moodRecord);
    }

    @PutMapping("/{id}")
    public Result<MoodRecord> updateMoodRecord(@PathVariable Long id, @Validated @RequestBody MoodRecordDTO moodRecordDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        MoodRecord moodRecord = moodRecordService.updateMoodRecord(id, userId, moodRecordDTO);
        return Result.success("更新成功", moodRecord);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMoodRecord(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        moodRecordService.deleteMoodRecord(id, userId);
        return Result.success();
    }

    @GetMapping("/today")
    public Result<Map<String, Object>> getTodayMoodRecord() {
        Long userId = SecurityUtils.getCurrentUserId();
        MoodRecord moodRecord = moodRecordService.getTodayMoodRecord(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("moodRecord", moodRecord);
        result.put("hasRecord", moodRecord != null);

        return Result.success(result);
    }

    @GetMapping("/recent/{days}")
    public Result<List<MoodRecord>> getRecentMoodRecords(@PathVariable Integer days) {
        if (days == null || days < 1) {
            days = 7;
        }
        if (days > 30) {
            days = 30;
        }
        Long userId = SecurityUtils.getCurrentUserId();
        List<MoodRecord> moodRecords = moodRecordService.getRecentMoodRecords(userId, days);
        return Result.success(moodRecords);
    }

    @GetMapping("/list")
    public Result<Page<MoodRecord>> getMoodRecordList(@Validated MoodRecordQueryDTO query) {
        Long userId = SecurityUtils.getCurrentUserId();
        query.setUserId(userId);
        Page<MoodRecord> moodRecordPage = moodRecordService.getMoodRecordPage(query);
        return Result.success(moodRecordPage);
    }

    @GetMapping("/{id}")
    public Result<MoodRecord> getMoodRecordDetail(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        MoodRecord moodRecord = moodRecordService.getById(id);
        if (moodRecord == null || !moodRecord.getUserId().equals(userId)) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }
        return Result.success(moodRecord);
    }
}
