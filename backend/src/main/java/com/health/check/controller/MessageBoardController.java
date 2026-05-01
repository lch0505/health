package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.entity.MessageBoard;
import com.health.check.entity.User;
import com.health.check.enums.ResponseCode;
import com.health.check.service.MessageBoardService;
import com.health.check.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message-board")
public class MessageBoardController {

    @Autowired
    private MessageBoardService messageBoardService;

    @GetMapping("/list")
    public Result<Page<MessageBoard>> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<MessageBoard> result = messageBoardService.getUserPage(page, size);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<MessageBoard> getById(@PathVariable Long id) {
        MessageBoard messageBoard = messageBoardService.getById(id);
        if (messageBoard == null || messageBoard.getStatus() != 1) {
            return Result.error(404, "留言不存在或已隐藏");
        }
        return Result.success(messageBoard);
    }

    @PostMapping("/add")
    @Transactional
    public Result<MessageBoard> add(@RequestBody MessageBoard messageBoard) {
        if (messageBoard.getContent() == null || messageBoard.getContent().trim().isEmpty()) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "留言内容不能为空");
        }
        
        User currentUser = SecurityUtils.getCurrentUser();
        
        messageBoard.setUserId(currentUser.getId());
        messageBoard.setUsername(currentUser.getUsername());
        messageBoard.setNickname(currentUser.getNickname() != null ? currentUser.getNickname() : currentUser.getUsername());
        messageBoard.setStatus(1);
        
        messageBoardService.save(messageBoard);
        return Result.success("留言发布成功", messageBoard);
    }

    @PutMapping("/update")
    @Transactional
    public Result<MessageBoard> update(@RequestBody MessageBoard messageBoard) {
        if (messageBoard.getId() == null) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "ID不能为空");
        }
        if (messageBoard.getContent() == null || messageBoard.getContent().trim().isEmpty()) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "留言内容不能为空");
        }
        
        MessageBoard existing = messageBoardService.getById(messageBoard.getId());
        if (existing == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "留言不存在");
        }
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!existing.getUserId().equals(currentUserId)) {
            return Result.error(ResponseCode.FORBIDDEN.getCode(), "无权编辑他人的留言");
        }
        
        existing.setContent(messageBoard.getContent());
        messageBoardService.updateById(existing);
        return Result.success("留言更新成功", existing);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Long id) {
        MessageBoard messageBoard = messageBoardService.getById(id);
        if (messageBoard == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "留言不存在");
        }
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!messageBoard.getUserId().equals(currentUserId)) {
            return Result.error(ResponseCode.FORBIDDEN.getCode(), "无权删除他人的留言");
        }
        
        messageBoardService.removeById(id);
        return Result.success("留言删除成功");
    }
}
