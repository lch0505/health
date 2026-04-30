package com.health.check.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.check.dto.LoginDTO;
import com.health.check.dto.RegisterDTO;
import com.health.check.dto.query.UserQueryDTO;
import com.health.check.entity.User;
import com.health.check.vo.LoginVO;

public interface UserService extends IService<User> {
    LoginVO login(LoginDTO loginDTO);

    User register(RegisterDTO registerDTO);

    User getByUsername(String username);

    Page<User> getUserPage(UserQueryDTO query);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    void updateStatus(Long id, Integer status);
}
