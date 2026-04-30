package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.query.AchievementQueryDTO;
import com.health.check.entity.Achievement;
import com.health.check.entity.UserAchievement;
import com.health.check.enums.RecordStatus;
import com.health.check.enums.ResponseCode;
import com.health.check.service.AchievementService;
import com.health.check.service.UserAchievementService;
import com.health.check.vo.AchievementVO;
import com.health.check.vo.UserAchievementVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/achievement")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAchievementController {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private UserAchievementService userAchievementService;

    @GetMapping("/list")
    public Result<Page<AchievementVO>> getAchievementList(@Validated AchievementQueryDTO query) {
        Page<Achievement> achievementPage = achievementService.getAchievementPage(
                query.getPage(),
                query.getSize(),
                query.getName(),
                query.getStatus()
        );

        Page<AchievementVO> voPage = new Page<>(achievementPage.getCurrent(), achievementPage.getSize(), achievementPage.getTotal());
        List<AchievementVO> voList = new ArrayList<>();

        for (Achievement achievement : achievementPage.getRecords()) {
            AchievementVO vo = new AchievementVO();
            BeanUtils.copyProperties(achievement, vo);
            voList.add(vo);
        }

        voPage.setRecords(voList);
        return Result.success(voPage);
    }

    @GetMapping("/{id}")
    public Result<AchievementVO> getAchievementDetail(@PathVariable Long id) {
        Achievement achievement = achievementService.getById(id);
        if (achievement == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }

        AchievementVO vo = new AchievementVO();
        BeanUtils.copyProperties(achievement, vo);
        return Result.success(vo);
    }

    @PostMapping
    @Transactional
    public Result<AchievementVO> createAchievement(@RequestBody Achievement achievement) {
        achievement.setStatus(RecordStatus.COMPLETED.getCode());
        achievementService.save(achievement);

        AchievementVO vo = new AchievementVO();
        BeanUtils.copyProperties(achievement, vo);
        return Result.success("创建成功", vo);
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<AchievementVO> updateAchievement(@PathVariable Long id, @RequestBody Achievement achievement) {
        Achievement existAchievement = achievementService.getById(id);
        if (existAchievement == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }

        achievement.setId(id);
        achievementService.updateById(achievement);

        AchievementVO vo = new AchievementVO();
        BeanUtils.copyProperties(achievementService.getById(id), vo);
        return Result.success("更新成功", vo);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> deleteAchievement(@PathVariable Long id) {
        Achievement achievement = achievementService.getById(id);
        if (achievement == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }

        achievementService.removeById(id);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    @Transactional
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Achievement achievement = achievementService.getById(id);
        if (achievement == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }

        achievement.setStatus(status);
        achievementService.updateById(achievement);
        return Result.success();
    }

    @GetMapping("/user-record/list")
    public Result<Page<UserAchievementVO>> getUserAchievementList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long achievementId) {

        Page<UserAchievement> userAchievementPage = userAchievementService.getUserAchievementPage(
                page, size, userId, achievementId, null, null
        );

        Page<UserAchievementVO> voPage = new Page<>(userAchievementPage.getCurrent(), userAchievementPage.getSize(), userAchievementPage.getTotal());
        List<UserAchievementVO> voList = new ArrayList<>();

        for (UserAchievement userAchievement : userAchievementPage.getRecords()) {
            UserAchievementVO vo = new UserAchievementVO();
            BeanUtils.copyProperties(userAchievement, vo);

            Achievement achievement = achievementService.getById(userAchievement.getAchievementId());
            if (achievement != null) {
                vo.setAchievementName(achievement.getName());
                vo.setAchievementDescription(achievement.getDescription());
                vo.setAchievementIcon(achievement.getIcon());
                vo.setAchievementPointsReward(achievement.getPointsReward());
            }

            voList.add(vo);
        }

        voPage.setRecords(voList);
        return Result.success(voPage);
    }

    @PostMapping("/grant")
    @Transactional
    public Result<UserAchievementVO> grantAchievement(
            @RequestParam Long userId,
            @RequestParam Long achievementId) {

        Achievement achievement = achievementService.getById(achievementId);
        if (achievement == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), "勋章不存在");
        }

        if (userAchievementService.hasAchievement(userId, achievementId)) {
            return Result.error(ResponseCode.CONFLICT.getCode(), "用户已拥有该勋章");
        }

        UserAchievement userAchievement = userAchievementService.grantAchievement(
                userId, achievementId, achievement.getAchievementCode(), LocalDate.now()
        );

        if (achievement.getPointsReward() > 0) {
            Achievement finalAchievement = achievement;
        }

        UserAchievementVO vo = new UserAchievementVO();
        BeanUtils.copyProperties(userAchievement, vo);
        vo.setAchievementName(achievement.getName());
        vo.setAchievementDescription(achievement.getDescription());
        vo.setAchievementPointsReward(achievement.getPointsReward());

        return Result.success("授予成功", vo);
    }

    @DeleteMapping("/user-record/{id}")
    @Transactional
    public Result<Void> revokeUserAchievement(@PathVariable Long id) {
        UserAchievement userAchievement = userAchievementService.getById(id);
        if (userAchievement == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }

        userAchievementService.removeById(id);
        return Result.success();
    }
}
