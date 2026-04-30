package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.query.PointsRecordQueryDTO;
import com.health.check.entity.PointsRecord;
import com.health.check.entity.UserPoints;
import com.health.check.enums.PointsType;
import com.health.check.service.PointsRecordService;
import com.health.check.service.UserPointsService;
import com.health.check.util.SecurityUtils;
import com.health.check.vo.PointsRecordVO;
import com.health.check.vo.UserPointsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/points")
public class PointsController {

    @Autowired
    private UserPointsService userPointsService;

    @Autowired
    private PointsRecordService pointsRecordService;

    @GetMapping("/summary")
    public Result<Map<String, Object>> getPointsSummary() {
        Long userId = SecurityUtils.getCurrentUserId();
        UserPoints userPoints = userPointsService.createOrGetUserPoints(userId);

        Map<String, Object> result = new HashMap<>();

        UserPointsVO vo = new UserPointsVO();
        BeanUtils.copyProperties(userPoints, vo);
        result.put("points", vo);

        long newAchievements = 0;
        result.put("newAchievements", newAchievements);

        return Result.success(result);
    }

    @GetMapping("/record/list")
    public Result<Page<PointsRecordVO>> getPointsRecordList(@Validated PointsRecordQueryDTO query) {
        Long userId = SecurityUtils.getCurrentUserId();

        Page<PointsRecord> recordPage = pointsRecordService.getPageByUserId(
                userId,
                query.getPage(),
                query.getSize(),
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
}
