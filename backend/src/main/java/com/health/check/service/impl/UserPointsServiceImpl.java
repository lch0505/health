package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.entity.PointsRecord;
import com.health.check.entity.UserPoints;
import com.health.check.enums.PointsType;
import com.health.check.mapper.UserPointsMapper;
import com.health.check.service.PointsRecordService;
import com.health.check.service.UserPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserPointsServiceImpl extends ServiceImpl<UserPointsMapper, UserPoints> implements UserPointsService {

    @Autowired
    private PointsRecordService pointsRecordService;

    @Override
    public UserPoints getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<UserPoints>()
                .eq(UserPoints::getUserId, userId)
                .last("LIMIT 1"));
    }

    @Override
    public UserPoints createOrGetUserPoints(Long userId) {
        UserPoints userPoints = getByUserId(userId);
        if (userPoints == null) {
            userPoints = new UserPoints();
            userPoints.setUserId(userId);
            userPoints.setTotalPoints(0);
            userPoints.setCurrentPoints(0);
            userPoints.setUsedPoints(0);
            save(userPoints);
        }
        return userPoints;
    }

    @Override
    @Transactional
    public void addPoints(Long userId, Integer points, String pointsType, String description, Long referenceId) {
        UserPoints userPoints = createOrGetUserPoints(userId);

        userPoints.setTotalPoints(userPoints.getTotalPoints() + points);
        userPoints.setCurrentPoints(userPoints.getCurrentPoints() + points);
        updateById(userPoints);

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setPointsType(pointsType);
        record.setDescription(description);
        record.setReferenceId(referenceId);
        record.setRecordDate(LocalDate.now());
        pointsRecordService.save(record);
    }
}
