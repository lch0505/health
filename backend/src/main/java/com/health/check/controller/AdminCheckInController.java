package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.entity.CheckIn;
import com.health.check.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/check-in")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCheckInController {

    @Autowired
    private CheckInService checkInService;

    @GetMapping("/list")
    public Result<Page<CheckIn>> getCheckInList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String checkInType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<CheckIn> checkInPage = checkInService.getAllCheckInPage(page, size, userId, checkInType, startDate, endDate);
        return Result.success(checkInPage);
    }

    @GetMapping("/{id}")
    public Result<CheckIn> getCheckInDetail(@PathVariable Long id) {
        CheckIn checkIn = checkInService.getById(id);
        if (checkIn == null) {
            return Result.error(404, "记录不存在");
        }
        return Result.success(checkIn);
    }
}
