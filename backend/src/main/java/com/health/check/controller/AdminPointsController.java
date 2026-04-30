package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.query.PointsRecordQueryDTO;
import com.health.check.entity.PointsRecord;
import com.health.check.entity.UserPoints;
import com.health.check.enums.PointsType;
import com.health.check.enums.ResponseCode;
import com.health.check.service.PointsRecordService;
import com.health.check.service.UserPointsService;
import com.health.check.vo.PointsRecordVO;
import com.health.check.vo.UserPointsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/points")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPointsController {

    @Autowired
    private UserPointsService userPointsService;

    @Autowired
    private PointsRecordService pointsRecordService;

    @GetMapping("/user/list")
    public Result<Page<UserPointsVO>> getUserPointsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId) {

        Page<UserPoints> pointsPage = new Page<>(page, size);
        Page<UserPointsVO> voPage = new Page<>(page, size);

        Page<UserPoints> result = userPointsService.page(pointsPage);

        List<UserPointsVO> voList = new ArrayList<>();
        for (UserPoints points : result.getRecords()) {
            UserPointsVO vo = new UserPointsVO();
            BeanUtils.copyProperties(points, vo);
            voList.add(vo);
        }

        voPage.setRecords(voList);
        voPage.setTotal(result.getTotal());
        return Result.success(voPage);
    }

    @GetMapping("/user/{userId}")
    public Result<UserPointsVO> getUserPoints(@PathVariable Long userId) {
        UserPoints userPoints = userPointsService.createOrGetUserPoints(userId);
        UserPointsVO vo = new UserPointsVO();
        BeanUtils.copyProperties(userPoints, vo);
        return Result.success(vo);
    }

    @GetMapping("/record/list")
    public Result<Page<PointsRecordVO>> getPointsRecordList(@Validated PointsRecordQueryDTO query) {
        Page<PointsRecord> recordPage = pointsRecordService.getAllPointsRecordPage(
                query.getPage(),
                query.getSize(),
                query.getUserId(),
                query.getStartDate(),
                query.getEndDate()
        );

        Page<PointsRecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        List<PointsRecordVO> voList = new ArrayList<>();

        for (PointsRecord record : recordPage.getRecords()) {
            PointsRecordVO vo = new PointsRecordVO();
            BeanUtils.copyProperties(record, vo);

            PointsType pointsType = PointsType.getByCode(record.getPointsType());
            if (pointsType != null) {
                vo.setPointsTypeName(pointsType.getDescription());
            }

            voList.add(vo);
        }

        voPage.setRecords(voList);
        return Result.success(voPage);
    }

    @PostMapping("/adjust")
    @Transactional
    public Result<Map<String, Object>> adjustPoints(
            @RequestParam Long userId,
            @RequestParam Integer points,
            @RequestParam String description) {

        if (points == null || points == 0) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "积分调整数量不能为0");
        }

        UserPoints userPoints = userPointsService.createOrGetUserPoints(userId);

        if (points < 0 && userPoints.getCurrentPoints() + points < 0) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "积分不足，无法扣除");
        }

        userPointsService.addPoints(
                userId,
                points,
                points > 0 ? PointsType.GOAL_COMPLETE.getCode() : PointsType.EXCHANGE.getCode(),
                description,
                null
        );

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("adjustedPoints", points);
        result.put("currentPoints", userPoints.getCurrentPoints() + points);

        return Result.success("积分调整成功", result);
    }
}
