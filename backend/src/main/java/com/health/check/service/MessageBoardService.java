package com.health.check.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.entity.MessageBoard;

public interface MessageBoardService extends IService<MessageBoard> {
    
    Page<MessageBoard> getAdminPage(Integer page, Integer size, String nickname, Integer status);
    
    Page<MessageBoard> getUserPage(Integer page, Integer size);
}
