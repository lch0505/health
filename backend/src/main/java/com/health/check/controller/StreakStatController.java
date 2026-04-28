package com.health.check.controller;

import com.health.check.common.Result;
import com.health.check.entity.StreakStat;
import com.health.check.service.StreakStatService;
import com.health.check.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/streak")
public class StreakStatController {

    @Autowired
    private StreakStatService streakStatService;

    @Autowired
    private UserService userService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getByUsername(username).getId();
    }

    @GetMapping
    public Result<Map<String, Object>> getStreakStats() {
        Long userId = getCurrentUserId();
        List<StreakStat> stats = streakStatService.getByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("stats", stats);

        int maxTotalStreak = 0;
        int currentTotalStreak = 0;

        for (StreakStat stat : stats) {
            if (stat.getCurrentStreak() > currentTotalStreak) {
                currentTotalStreak = stat.getCurrentStreak();
            }
            if (stat.getMaxStreak() > maxTotalStreak) {
                maxTotalStreak = stat.getMaxStreak();
            }
        }

        result.put("maxTotalStreak", maxTotalStreak);
        result.put("currentTotalStreak", currentTotalStreak);

        return Result.success(result);
    }
}
