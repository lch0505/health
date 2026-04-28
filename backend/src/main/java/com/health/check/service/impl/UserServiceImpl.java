package com.health.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.check.dto.LoginDTO;
import com.health.check.dto.RegisterDTO;
import com.health.check.entity.User;
import com.health.check.exception.BusinessException;
import com.health.check.mapper.UserMapper;
import com.health.check.service.UserService;
import com.health.check.util.JwtUtil;
import com.health.check.vo.LoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        User user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(403, "用户已被禁用");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        LoginVO loginVO = new LoginVO();
        BeanUtils.copyProperties(user, loginVO);
        loginVO.setToken(token);

        return loginVO;
    }

    @Override
    public User register(RegisterDTO registerDTO) {
        User existUser = getByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException(400, "用户名已存在");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(StringUtils.hasText(registerDTO.getNickname()) ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setRole("user");
        user.setStatus(1);

        save(user);
        return user;
    }

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0));
    }

    @Override
    public Page<User> getUserPage(Integer page, Integer size, String username, String role) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username).or().like(User::getNickname, username);
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        wrapper.eq(User::getDeleted, 0).orderByDesc(User::getCreateTime);

        return page(pageParam, wrapper);
    }

    @Override
    public User createUser(User user) {
        User existUser = getByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException(400, "用户名已存在");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (!StringUtils.hasText(user.getRole())) {
            user.setRole("user");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        save(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User existUser = getById(user.getId());
        if (existUser == null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }

        updateById(user);
        return getById(user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        removeById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setStatus(status);
        updateById(user);
    }
}
