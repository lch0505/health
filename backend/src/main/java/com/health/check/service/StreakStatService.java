package com.health.check.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.entity.StreakStat;

import java.util.List;

public interface StreakStatService extends IService<StreakStat> {
    StreakStat getByUserIdAndType(Long userId, String streakType);

    List<StreakStat> getByUserId(Long userId);
}
