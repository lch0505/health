package com.health.check.config;

import com.health.check.entity.User;
import com.health.check.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initAdminUser();
        initTestUser();
    }

    private void initAdminUser() {
        User admin = userService.getByUsername("admin");
        if (admin == null) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setNickname("系统管理员");
            user.setRole("admin");
            user.setStatus(1);
            userService.save(user);
            System.out.println("========================================");
            System.out.println("管理员账号已创建: admin / admin123");
            System.out.println("========================================");
        }
    }

    private void initTestUser() {
        User testUser = userService.getByUsername("user");
        if (testUser == null) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setNickname("普通用户");
            user.setRole("user");
            user.setStatus(1);
            userService.save(user);
            System.out.println("========================================");
            System.out.println("测试用户已创建: user / user123");
            System.out.println("========================================");
        }
    }
}
