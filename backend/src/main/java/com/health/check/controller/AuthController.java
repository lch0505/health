package com.health.check.controller;

import com.health.check.common.Result;
import com.health.check.dto.LoginDTO;
import com.health.check.dto.RegisterDTO;
import com.health.check.entity.User;
import com.health.check.service.UserService;
import com.health.check.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }

    @PostMapping("/register")
    public Result<User> register(@Validated @RequestBody RegisterDTO registerDTO) {
        User user = userService.register(registerDTO);
        user.setPassword(null);
        return Result.success("注册成功", user);
    }
}
