package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.VitalSignDTO;
import com.health.check.dto.query.AdminVitalSignQueryDTO;
import com.health.check.entity.VitalSign;
import com.health.check.enums.ResponseCode;
import com.health.check.service.VitalSignService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/vital-sign")
@PreAuthorize("hasRole('ADMIN')")
public class AdminVitalSignController {

    @Autowired
    private VitalSignService vitalSignService;

    @GetMapping("/list")
    public Result<Page<VitalSign>> getVitalSignList(@Validated AdminVitalSignQueryDTO query) {
        Page<VitalSign> vitalSignPage = vitalSignService.getAllVitalSignPage(query);
        return Result.success(vitalSignPage);
    }

    @GetMapping("/{id}")
    public Result<VitalSign> getVitalSignDetail(@PathVariable Long id) {
        VitalSign vitalSign = vitalSignService.getById(id);
        if (vitalSign == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }
        return Result.success(vitalSign);
    }

    @PostMapping
    @Transactional
    public Result<VitalSign> addVitalSign(@RequestParam Long userId, @Validated @RequestBody VitalSignDTO vitalSignDTO) {
        LocalDate today = LocalDate.now();
        VitalSign existRecord = vitalSignService.getVitalSignByDate(userId, today);
        if (existRecord != null) {
            return Result.error(ResponseCode.RECORD_EXISTS.getCode(), ResponseCode.RECORD_EXISTS.getMessage());
        }

        VitalSign vitalSign = new VitalSign();
        BeanUtils.copyProperties(vitalSignDTO, vitalSign);
        vitalSign.setUserId(userId);
        vitalSign.setRecordDate(today);

        vitalSignService.save(vitalSign);
        return Result.success("添加成功", vitalSign);
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<VitalSign> updateVitalSign(@PathVariable Long id, @Validated @RequestBody VitalSignDTO vitalSignDTO) {
        VitalSign vitalSign = vitalSignService.getById(id);
        if (vitalSign == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }

        if (vitalSignDTO.getWeight() != null) {
            vitalSign.setWeight(vitalSignDTO.getWeight());
        }
        if (vitalSignDTO.getBodyFat() != null) {
            vitalSign.setBodyFat(vitalSignDTO.getBodyFat());
        }
        if (vitalSignDTO.getSystolicPressure() != null) {
            vitalSign.setSystolicPressure(vitalSignDTO.getSystolicPressure());
        }
        if (vitalSignDTO.getDiastolicPressure() != null) {
            vitalSign.setDiastolicPressure(vitalSignDTO.getDiastolicPressure());
        }
        if (vitalSignDTO.getVisionStatus() != null) {
            vitalSign.setVisionStatus(vitalSignDTO.getVisionStatus());
        }
        if (vitalSignDTO.getSleepQuality() != null) {
            vitalSign.setSleepQuality(vitalSignDTO.getSleepQuality());
        }
        if (vitalSignDTO.getRemark() != null) {
            vitalSign.setRemark(vitalSignDTO.getRemark());
        }

        vitalSignService.updateById(vitalSign);
        return Result.success("更新成功", vitalSignService.getById(id));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> deleteVitalSign(@PathVariable Long id) {
        VitalSign vitalSign = vitalSignService.getById(id);
        if (vitalSign == null) {
            return Result.error(ResponseCode.RECORD_NOT_FOUND.getCode(), ResponseCode.RECORD_NOT_FOUND.getMessage());
        }

        vitalSignService.removeById(id);
        return Result.success();
    }
}
