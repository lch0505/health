package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.entity.Achievement;
import com.health.check.enums.RecordStatus;
import com.health.check.mapper.AchievementMapper;
import com.health.check.service.AchievementService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AchievementServiceImpl extends ServiceImpl<AchievementMapper, Achievement> implements AchievementService {

    @Override
    public List<Achievement> getEnabledAchievements() {
        return list(new LambdaQueryWrapper<Achievement>()
                .eq(Achievement::getStatus, RecordStatus.COMPLETED.getCode())
                .orderByAsc(Achievement::getSortOrder));
    }

    @Override
    public Achievement getByCode(String achievementCode) {
        return getOne(new LambdaQueryWrapper<Achievement>()
                .eq(Achievement::getAchievementCode, achievementCode)
                .last("LIMIT 1"));
    }

    @Override
    public Page<Achievement> getAchievementPage(Integer page, Integer size, String name, Integer status) {
        Page<Achievement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Achievement> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(name)) {
            wrapper.like(Achievement::getName, name)
                    .or().like(Achievement::getDescription, name);
        }
        if (status != null) {
            wrapper.eq(Achievement::getStatus, status);
        }

        wrapper.orderByAsc(Achievement::getSortOrder)
                .orderByDesc(Achievement::getCreateTime);

        return page(pageParam, wrapper);
    }
}
