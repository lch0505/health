-- 创建数据库
CREATE DATABASE IF NOT EXISTS health_check DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE health_check;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin-管理员，user-普通用户',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 打卡记录表
CREATE TABLE IF NOT EXISTS check_in (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '打卡ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    check_in_date DATE NOT NULL COMMENT '打卡日期',
    check_in_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间',
    check_in_type VARCHAR(20) NOT NULL COMMENT '打卡类型：wake_up-起床，sleep-睡觉',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_check_in_date (check_in_date),
    INDEX idx_user_date (user_id, check_in_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡记录表';

-- 健康记录表
CREATE TABLE IF NOT EXISTS health_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    record_date DATE NOT NULL COMMENT '记录日期',
    record_type VARCHAR(20) NOT NULL COMMENT '记录类型：exercise-运动，water-饮水，sleep_early-早睡',
    quantity INT COMMENT '数量（运动：分钟，饮水：毫升）',
    duration INT COMMENT '时长（分钟）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-未完成，1-已完成',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_record_date (record_date),
    INDEX idx_record_type (record_type),
    INDEX idx_user_date_type (user_id, record_date, record_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康记录表';

-- 连续打卡统计表
CREATE TABLE IF NOT EXISTS streak_stat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    streak_type VARCHAR(20) NOT NULL COMMENT '打卡类型：wake_up-起床，sleep-睡觉，all-全部',
    current_streak INT NOT NULL DEFAULT 0 COMMENT '当前连续打卡天数',
    max_streak INT NOT NULL DEFAULT 0 COMMENT '最长连续打卡天数',
    last_check_in_date DATE COMMENT '最后打卡日期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_type (user_id, streak_type),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='连续打卡统计表';

-- 初始化管理员账号（密码：admin123，已加密）
INSERT INTO user (username, password, nickname, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '系统管理员', 'admin', 1);

-- 初始化普通用户账号（密码：user123，已加密）
INSERT INTO user (username, password, nickname, role, status) VALUES
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '普通用户', 'user', 1);
