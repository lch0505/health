package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.entity.StreakStat;
import com.health.check.mapper.StreakStatMapper;
import com.health.check.service.StreakStatService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StreakStatServiceImpl extends ServiceImpl<StreakStatMapper, StreakStat> implements StreakStatService {

    @Override
    public StreakStat getByUserIdAndType(Long userId, String streakType) {
        return getOne(new LambdaQueryWrapper<StreakStat>()
                .eq(StreakStat::getUserId, userId)
                .eq(StreakStat::getStreakType, streakType)
                .last("LIMIT 1"));
    }

    @Override
    public List<StreakStat> getByUserId(Long userId) {
        return list(new LambdaQueryWrapper<StreakStat>()
                .eq(StreakStat::getUserId, userId));
    }
}
