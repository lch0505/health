package com.health.check.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.check.common.Result;
import com.health.check.dto.UpdateStatusDTO;
import com.health.check.dto.query.UserQueryDTO;
import com.health.check.entity.User;
import com.health.check.service.UserService;
import com.health.check.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/info")
    public Result<User> getCurrentUser() {
        User user = SecurityUtils.getCurrentUser();
        return Result.success(user);
    }

    @PutMapping("/user/info")
    public Result<User> updateCurrentUser(@RequestBody User user) {
        User currentUser = SecurityUtils.getCurrentUser();

        user.setId(currentUser.getId());
        user.setUsername(null);
        user.setPassword(null);
        user.setRole(null);
        user.setStatus(null);

        User updated = userService.updateUser(user);
        updated.setPassword(null);
        return Result.success("更新成功", updated);
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<User>> getUserPage(@Validated UserQueryDTO query) {
        Page<User> userPage = userService.getUserPage(query);
        userPage.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(userPage);
    }

    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PostMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        created.setPassword(null);
        return Result.success("创建成功", created);
    }

    @PutMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        User updated = userService.updateUser(user);
        updated.setPassword(null);
        return Result.success("更新成功", updated);
    }

    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/admin/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @Validated @RequestBody UpdateStatusDTO updateStatusDTO) {
        userService.updateStatus(id, updateStatusDTO.getStatus());
        return Result.success();
    }
}
