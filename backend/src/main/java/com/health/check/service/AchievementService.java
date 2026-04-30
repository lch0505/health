package com.health.check.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.entity.Achievement;

import java.util.List;

public interface AchievementService extends IService<Achievement> {
    List<Achievement> getEnabledAchievements();

    Achievement getByCode(String achievementCode);

    Page<Achievement> getAchievementPage(Integer page, Integer size, String name, Integer status);
}
