package com.health.check.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.dto.MoodRecordDTO;
import com.health.check.dto.query.AdminMoodRecordQueryDTO;
import com.health.check.dto.query.MoodRecordQueryDTO;
import com.health.check.entity.MoodRecord;

import java.time.LocalDate;
import java.util.List;

public interface MoodRecordService extends IService<MoodRecord> {
    MoodRecord addMoodRecord(Long userId, MoodRecordDTO moodRecordDTO);

    MoodRecord updateMoodRecord(Long id, Long userId, MoodRecordDTO moodRecordDTO);

    void deleteMoodRecord(Long id, Long userId);

    Page<MoodRecord> getMoodRecordPage(MoodRecordQueryDTO query);

    Page<MoodRecord> getAllMoodRecordPage(AdminMoodRecordQueryDTO query);

    MoodRecord getTodayMoodRecord(Long userId);

    MoodRecord getMoodRecordByDate(Long userId, LocalDate date);

    List<MoodRecord> getRecentMoodRecords(Long userId, int days);
}
