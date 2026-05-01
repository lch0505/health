package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.entity.MessageBoard;
import com.health.check.enums.ResponseCode;
import com.health.check.service.MessageBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/message-board")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMessageBoardController {

    @Autowired
    private MessageBoardService messageBoardService;

    @GetMapping("/list")
    public Result<Page<MessageBoard>> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Integer status) {
        
        Page<MessageBoard> result = messageBoardService.getAdminPage(page, size, nickname, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<MessageBoard> getById(@PathVariable Long id) {
        MessageBoard messageBoard = messageBoardService.getById(id);
        if (messageBoard == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "留言不存在");
        }
        return Result.success(messageBoard);
    }

    @PutMapping("/update")
    @Transactional
    public Result<MessageBoard> update(@RequestBody MessageBoard messageBoard) {
        if (messageBoard.getId() == null) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "ID不能为空");
        }
        
        MessageBoard existing = messageBoardService.getById(messageBoard.getId());
        if (existing == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "留言不存在");
        }
        
        messageBoardService.updateById(messageBoard);
        return Result.success("留言更新成功", messageBoard);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Long id) {
        MessageBoard messageBoard = messageBoardService.getById(id);
        if (messageBoard == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "留言不存在");
        }
        
        messageBoardService.removeById(id);
        return Result.success("留言删除成功");
    }

    @PutMapping("/status/{id}")
    @Transactional
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        MessageBoard messageBoard = messageBoardService.getById(id);
        if (messageBoard == null) {
            return Result.error(ResponseCode.NOT_FOUND.getCode(), "留言不存在");
        }
        
        if (status != 0 && status != 1) {
            return Result.error(ResponseCode.BAD_REQUEST.getCode(), "状态值无效");
        }
        
        messageBoard.setStatus(status);
        messageBoardService.updateById(messageBoard);
        return Result.success("状态更新成功");
    }
}
