package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.query.AdminCheckInQueryDTO;
import com.health.check.entity.CheckIn;
import com.health.check.enums.ResponseCode;
import com.health.check.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/check-in")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCheckInController {

    @Autowired
    private CheckInService checkInService;

    @GetMapping("/list")
    public Result<Page<CheckIn>> getCheckInList(@Validated AdminCheckInQueryDTO query) {
        Page<CheckIn> checkInPage = checkInService.getAllCheckInPage(query);
        return Result.success(checkInPage);
    }

    @GetMapping("/{id}")
    public Result<CheckIn> getCheckInDetail(@PathVariable Long id) {
        CheckIn checkIn = checkInService.getById(id);
        if (checkIn == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }
        return Result.success(checkIn);
    }
}
