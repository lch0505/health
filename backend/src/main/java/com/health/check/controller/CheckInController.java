package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.CheckInDTO;
import com.health.check.dto.query.CheckInQueryDTO;
import com.health.check.entity.CheckIn;
import com.health.check.service.CheckInService;
import com.health.check.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user/check-in")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @PostMapping
    public Result<CheckIn> checkIn(@Validated @RequestBody CheckInDTO checkInDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        CheckIn checkIn = checkInService.checkIn(userId, checkInDTO);
        return Result.success("打卡成功", checkIn);
    }

    @GetMapping("/today")
    public Result<Map<String, Object>> getTodayStatus() {
        Long userId = SecurityUtils.getCurrentUserId();
        Map<String, Object> result = new HashMap<>();

        CheckIn wakeUp = checkInService.getTodayCheckIn(userId, "wake_up");
        CheckIn sleep = checkInService.getTodayCheckIn(userId, "sleep");

        result.put("wakeUp", wakeUp);
        result.put("sleep", sleep);
        result.put("wakeUpChecked", wakeUp != null);
        result.put("sleepChecked", sleep != null);

        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<Page<CheckIn>> getCheckInList(@Validated CheckInQueryDTO query) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<CheckIn> checkInPage = checkInService.getCheckInPage(
                userId, query.getPage(), query.getSize(), 
                query.getCheckInType(), query.getStartDate(), query.getEndDate());
        return Result.success(checkInPage);
    }

    @GetMapping("/{id}")
    public Result<CheckIn> getCheckInDetail(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        CheckIn checkIn = checkInService.getById(id);
        if (checkIn == null || !checkIn.getUserId().equals(userId)) {
            return Result.error(404, "记录不存在");
        }
        return Result.success(checkIn);
    }
}
