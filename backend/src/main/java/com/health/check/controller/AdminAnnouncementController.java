package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.entity.Announcement;
import com.health.check.entity.User;
import com.health.check.enums.ResponseCode;
import com.health.check.service.AnnouncementService;
import com.health.check.service.UserService;
import com.health.check.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/announcement")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<Page<Announcement>> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {
        
        Page<Announcement> result = announcementService.getAdminPage(page, size, title, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Announcement> getById(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "公告不存在");
        }
        return Result.success(announcement);
    }

    @PostMapping("/add")
    @Transactional
    public Result<Announcement> add(@RequestBody Announcement announcement) {
        if (announcement.getTitle() == null || announcement.getContent() == null) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "标题和内容不能为空");
        }
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userService.getById(currentUserId);
        
        announcement.setPublisherId(currentUserId);
        announcement.setPublisherName(currentUser.getNickname() != null ? currentUser.getNickname() : currentUser.getUsername());
        if (announcement.getStatus() == null) {
            announcement.setStatus(1);
        }
        
        announcementService.save(announcement);
        return Result.success("公告发布成功", announcement);
    }

    @PutMapping("/update")
    @Transactional
    public Result<Announcement> update(@RequestBody Announcement announcement) {
        if (announcement.getId() == null) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "ID不能为空");
        }
        
        Announcement existing = announcementService.getById(announcement.getId());
        if (existing == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "公告不存在");
        }
        
        announcementService.updateById(announcement);
        return Result.success("公告更新成功", announcement);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "公告不存在");
        }
        
        announcementService.removeById(id);
        return Result.success("公告删除成功");
    }

    @PutMapping("/status/{id}")
    @Transactional
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "公告不存在");
        }
        
        if (status != 0 && status != 1) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "状态值无效");
        }
        
        announcement.setStatus(status);
        announcementService.updateById(announcement);
        return Result.success("状态更新成功");
    }
}
