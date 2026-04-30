package com.health.check.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.entity.Announcement;

import java.util.List;

public interface AnnouncementService extends IService<Announcement> {
    
    Page<Announcement> getAdminPage(Integer page, Integer size, String title, Integer status);
    
    Page<Announcement> getUserPage(Integer page, Integer size);
    
    List<Announcement> getLatest(Integer limit);
}
