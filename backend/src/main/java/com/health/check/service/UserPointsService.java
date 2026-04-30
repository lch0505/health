package com.health.check.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.entity.UserPoints;

public interface UserPointsService extends IService<UserPoints> {
    UserPoints getByUserId(Long userId);

    UserPoints createOrGetUserPoints(Long userId);

    void addPoints(Long userId, Integer points, String pointsType, String description, Long referenceId);
}
