package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.entity.UserAchievement;
import com.health.check.enums.ResponseCode;
import com.health.check.exception.BusinessException;
import com.health.check.mapper.UserAchievementMapper;
import com.health.check.service.UserAchievementService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserAchievementServiceImpl extends ServiceImpl<UserAchievementMapper, UserAchievement> implements UserAchievementService {

    @Override
    public List<UserAchievement> getByUserId(Long userId) {
        return list(new LambdaQueryWrapper<UserAchievement>()
                .eq(UserAchievement::getUserId, userId)
                .orderByDesc(UserAchievement::getObtainDate)
                .orderByDesc(UserAchievement::getCreateTime));
    }

    @Override
    public boolean hasAchievement(Long userId, Long achievementId) {
        return count(new LambdaQueryWrapper<UserAchievement>()
                .eq(UserAchievement::getUserId, userId)
                .eq(UserAchievement::getAchievementId, achievementId)) > 0;
    }

    @Override
    public UserAchievement grantAchievement(Long userId, Long achievementId, String achievementCode, LocalDate obtainDate) {
        if (hasAchievement(userId, achievementId)) {
            return null;
        }

        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUserId(userId);
        userAchievement.setAchievementId(achievementId);
        userAchievement.setAchievementCode(achievementCode);
        userAchievement.setObtainDate(obtainDate);
        userAchievement.setIsNew(1);
        save(userAchievement);
        return userAchievement;
    }

    @Override
    public Page<UserAchievement> getUserAchievementPage(Integer page, Integer size, Long userId, Long achievementId, LocalDate startDate, LocalDate endDate) {
        Page<UserAchievement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<UserAchievement> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(UserAchievement::getUserId, userId);
        }
        if (achievementId != null) {
            wrapper.eq(UserAchievement::getAchievementId, achievementId);
        }
        if (startDate != null) {
            wrapper.ge(UserAchievement::getObtainDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(UserAchievement::getObtainDate, endDate);
        }

        wrapper.orderByDesc(UserAchievement::getObtainDate)
                .orderByDesc(UserAchievement::getCreateTime);

        return page(pageParam, wrapper);
    }

    @Override
    public long countNewAchievements(Long userId) {
        return count(new LambdaQueryWrapper<UserAchievement>()
                .eq(UserAchievement::getUserId, userId)
                .eq(UserAchievement::getIsNew, 1));
    }

    @Override
    public void markAsRead(Long userId, Long userAchievementId) {
        UserAchievement userAchievement = getById(userAchievementId);
        if (userAchievement == null || !userAchievement.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.RECORD_NOT_FOUND);
        }

        update(new LambdaUpdateWrapper<UserAchievement>()
                .eq(UserAchievement::getId, userAchievementId)
                .set(UserAchievement::getIsNew, 0));
    }
}
