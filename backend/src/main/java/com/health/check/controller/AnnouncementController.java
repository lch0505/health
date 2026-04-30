package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.entity.Announcement;
import com.health.check.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/list")
    public Result<Page<Announcement>> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<Announcement> result = announcementService.getUserPage(page, size);
        return Result.success(result);
    }

    @GetMapping("/latest")
    public Result<List<Announcement>> getLatest(
            @RequestParam(defaultValue = "5") Integer limit) {
        
        List<Announcement> announcements = announcementService.getLatest(limit);
        return Result.success(announcements);
    }

    @GetMapping("/{id}")
    public Result<Announcement> getById(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null || announcement.getStatus() != 1) {
            return Result.error(404, "公告不存在或已下架");
        }
        return Result.success(announcement);
    }
}
