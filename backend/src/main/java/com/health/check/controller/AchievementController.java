package com.health.check.controller;

import com.health.check.common.Result;
import com.health.check.entity.Achievement;
import com.health.check.entity.UserAchievement;
import com.health.check.enums.RequirementType;
import com.health.check.service.AchievementService;
import com.health.check.service.UserAchievementService;
import com.health.check.util.SecurityUtils;
import com.health.check.vo.AchievementVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/achievement")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private UserAchievementService userAchievementService;

    @GetMapping("/wall")
    public Result<Map<String, Object>> getAchievementWall() {
        Long userId = SecurityUtils.getCurrentUserId();

        List<Achievement> allAchievements = achievementService.getEnabledAchievements();
        List<UserAchievement> userAchievements = userAchievementService.getByUserId(userId);

        Map<Long, UserAchievement> userAchievementMap = userAchievements.stream()
                .collect(Collectors.toMap(UserAchievement::getAchievementId, ua -> ua));

        List<AchievementVO> voList = new ArrayList<>();
        int obtainedCount = 0;

        for (Achievement achievement : allAchievements) {
            AchievementVO vo = new AchievementVO();
            BeanUtils.copyProperties(achievement, vo);

            RequirementType requirementType = RequirementType.getByCode(achievement.getRequirementType());
            if (requirementType != null) {
                vo.setRequirementTypeName(requirementType.getDescription());
            }

            UserAchievement userAchievement = userAchievementMap.get(achievement.getId());
            if (userAchievement != null) {
                vo.setObtained(true);
                vo.setObtainDate(userAchievement.getObtainDate());
                vo.setIsNew(userAchievement.getIsNew() == 1);
                obtainedCount++;
            } else {
                vo.setObtained(false);
                vo.setIsNew(false);
            }

            voList.add(vo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("achievements", voList);
        result.put("totalCount", allAchievements.size());
        result.put("obtainedCount", obtainedCount);
        result.put("newCount", userAchievementService.countNewAchievements(userId));

        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<List<AchievementVO>> getMyAchievements() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<UserAchievement> userAchievements = userAchievementService.getByUserId(userId);

        List<AchievementVO> voList = new ArrayList<>();
        for (UserAchievement userAchievement : userAchievements) {
            Achievement achievement = achievementService.getById(userAchievement.getAchievementId());
            if (achievement != null) {
                AchievementVO vo = new AchievementVO();
                BeanUtils.copyProperties(achievement, vo);

                RequirementType requirementType = RequirementType.getByCode(achievement.getRequirementType());
                if (requirementType != null) {
                    vo.setRequirementTypeName(requirementType.getDescription());
                }

                vo.setObtained(true);
                vo.setObtainDate(userAchievement.getObtainDate());
                vo.setIsNew(userAchievement.getIsNew() == 1);

                voList.add(vo);
            }
        }

        return Result.success(voList);
    }

    @PostMapping("/read/{id}")
    public Result<Void> markAsRead(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        userAchievementService.markAsRead(userId, id);
        return Result.success();
    }
}
