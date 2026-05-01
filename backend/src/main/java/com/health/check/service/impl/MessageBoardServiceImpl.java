package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.entity.MessageBoard;
import com.health.check.mapper.MessageBoardMapper;
import com.health.check.service.MessageBoardService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MessageBoardServiceImpl extends ServiceImpl<MessageBoardMapper, MessageBoard> implements MessageBoardService {
    
    @Override
    public Page<MessageBoard> getAdminPage(Integer page, Integer size, String nickname, Integer status) {
        Page<MessageBoard> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<MessageBoard> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(nickname)) {
            wrapper.like(MessageBoard::getNickname, nickname);
        }
        if (status != null) {
            wrapper.eq(MessageBoard::getStatus, status);
        }
        wrapper.orderByDesc(MessageBoard::getCreateTime);
        
        return this.page(pageParam, wrapper);
    }
    
    @Override
    public Page<MessageBoard> getUserPage(Integer page, Integer size) {
        Page<MessageBoard> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<MessageBoard> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(MessageBoard::getStatus, 1);
        wrapper.orderByDesc(MessageBoard::getCreateTime);
        
        return this.page(pageParam, wrapper);
    }
}
