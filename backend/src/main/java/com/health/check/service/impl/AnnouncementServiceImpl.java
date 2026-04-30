package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.entity.Announcement;
import com.health.check.mapper.AnnouncementMapper;
import com.health.check.service.AnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {
    
    @Override
    public Page<Announcement> getAdminPage(Integer page, Integer size, String title, Integer status) {
        Page<Announcement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(title)) {
            wrapper.like(Announcement::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }
        wrapper.orderByDesc(Announcement::getCreateTime);
        
        return this.page(pageParam, wrapper);
    }
    
    @Override
    public Page<Announcement> getUserPage(Integer page, Integer size) {
        Page<Announcement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Announcement::getStatus, 1);
        wrapper.orderByDesc(Announcement::getCreateTime);
        
        return this.page(pageParam, wrapper);
    }
    
    @Override
    public List<Announcement> getLatest(Integer limit) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Announcement::getStatus, 1);
        wrapper.orderByDesc(Announcement::getCreateTime);
        wrapper.last("LIMIT " + limit);
        
        return this.list(wrapper);
    }
}
