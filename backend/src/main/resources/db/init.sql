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

-- 体征数据表
CREATE TABLE IF NOT EXISTS vital_sign (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '体征数据ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    record_date DATE NOT NULL COMMENT '记录日期',
    weight DECIMAL(5,2) COMMENT '体重（kg）',
    body_fat DECIMAL(5,2) COMMENT '体脂率（%）',
    systolic_pressure INT COMMENT '收缩压（mmHg）',
    diastolic_pressure INT COMMENT '舒张压（mmHg）',
    vision_status VARCHAR(20) COMMENT '视力状态：excellent-优秀，good-良好，normal-一般，poor-较差',
    sleep_quality INT COMMENT '睡眠质量评分：1-非常差，2-较差，3-一般，4-良好，5-非常好',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_record_date (record_date),
    UNIQUE KEY uk_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体征数据表';

-- 饮食记录表
CREATE TABLE IF NOT EXISTS diet_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '饮食记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    record_date DATE NOT NULL COMMENT '记录日期',
    meal_type VARCHAR(20) NOT NULL COMMENT '餐次类型：breakfast-早餐，lunch-午餐，dinner-晚餐',
    diet_taste VARCHAR(20) COMMENT '饮食口味：light-清淡，moderate-适中，oily-油腻',
    avoid_food_compliance TINYINT COMMENT '忌口执行：0-未执行，1-已执行',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-未完成，1-已完成',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_record_date (record_date),
    INDEX idx_meal_type (meal_type),
    UNIQUE KEY uk_user_date_meal (user_id, record_date, meal_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='饮食记录表';

-- 情绪记录表
CREATE TABLE IF NOT EXISTS mood_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '情绪记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    record_date DATE NOT NULL COMMENT '记录日期',
    mood_type VARCHAR(20) NOT NULL COMMENT '心情类型：very_happy-非常开心，happy-开心，normal-一般，sad-难过，angry-生气',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_record_date (record_date),
    INDEX idx_mood_type (mood_type),
    UNIQUE KEY uk_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情绪记录表';

-- 用户积分表
CREATE TABLE IF NOT EXISTS user_points (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '积分ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_points INT NOT NULL DEFAULT 0 COMMENT '总积分',
    current_points INT NOT NULL DEFAULT 0 COMMENT '当前可用积分',
    used_points INT NOT NULL DEFAULT 0 COMMENT '已使用积分',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户积分表';

-- 积分记录表
CREATE TABLE IF NOT EXISTS points_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    points INT NOT NULL COMMENT '积分数量（正数为获得，负数为消耗）',
    points_type VARCHAR(50) NOT NULL COMMENT '积分类型：daily_check_in-每日打卡, continuous_streak-连续打卡, goal_complete-完成目标, exchange-兑换',
    description VARCHAR(255) COMMENT '描述',
    reference_id BIGINT COMMENT '关联记录ID',
    record_date DATE NOT NULL COMMENT '记录日期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_record_date (record_date),
    INDEX idx_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- 勋章定义表
CREATE TABLE IF NOT EXISTS achievement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '勋章ID',
    achievement_code VARCHAR(50) NOT NULL UNIQUE COMMENT '勋章代码：sleep_early_master-早睡达人, exercise_master-运动坚持, water_master-饮水自律, full_month-全月满卡',
    name VARCHAR(50) NOT NULL COMMENT '勋章名称',
    description VARCHAR(255) COMMENT '勋章描述',
    icon VARCHAR(255) COMMENT '勋章图标URL',
    requirement_type VARCHAR(50) NOT NULL COMMENT '条件类型：streak-连续天数, total-总次数, monthly-月度统计',
    requirement_value INT NOT NULL COMMENT '条件值（如连续30天）',
    requirement_description VARCHAR(255) COMMENT '条件描述',
    points_reward INT NOT NULL DEFAULT 0 COMMENT '获得勋章奖励积分',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_achievement_code (achievement_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='勋章定义表';

-- 用户获得的勋章表
CREATE TABLE IF NOT EXISTS user_achievement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    achievement_id BIGINT NOT NULL COMMENT '勋章ID',
    achievement_code VARCHAR(50) NOT NULL COMMENT '勋章代码',
    obtain_date DATE NOT NULL COMMENT '获得日期',
    is_new TINYINT NOT NULL DEFAULT 1 COMMENT '是否新获得：0-已读，1-新获得',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_achievement (user_id, achievement_id),
    INDEX idx_user_id (user_id),
    INDEX idx_achievement_id (achievement_id),
    INDEX idx_obtain_date (obtain_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户获得的勋章表';

-- 初始化勋章数据
INSERT INTO achievement (achievement_code, name, description, requirement_type, requirement_value, requirement_description, points_reward, sort_order, status) VALUES
('sleep_early_master', '早睡达人', '坚持早睡打卡，养成良好作息习惯', 'streak', 30, '连续30天早睡打卡', 500, 1, 1),
('exercise_master', '运动坚持', '坚持运动，保持健康体魄', 'streak', 30, '连续30天运动记录', 500, 2, 1),
('water_master', '饮水自律', '坚持每日饮水，保持充足水分', 'streak', 30, '连续30天饮水记录', 500, 3, 1),
('full_month', '全月满卡', '整个月坚持打卡，成就满满', 'monthly', 1, '自然月内每天都有打卡记录', 1000, 4, 1);

-- 积分配置表
CREATE TABLE IF NOT EXISTS points_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    points_type VARCHAR(50) NOT NULL UNIQUE COMMENT '积分类型：daily_check_in-每日打卡, continuous_streak_7-连续7天打卡, continuous_streak_15-连续15天打卡, continuous_streak_30-连续30天打卡, goal_complete-完成目标',
    points_type_name VARCHAR(50) NOT NULL COMMENT '积分类型名称',
    points INT NOT NULL DEFAULT 0 COMMENT '积分数量',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_points_type (points_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分配置表';

-- 公告表
CREATE TABLE IF NOT EXISTS announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    title VARCHAR(100) NOT NULL COMMENT '公告标题',
    content TEXT COMMENT '公告内容',
    publisher_id BIGINT NOT NULL COMMENT '发布者ID',
    publisher_name VARCHAR(50) COMMENT '发布者名称',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 初始化积分配置数据
INSERT INTO points_config (points_type, points_type_name, points, description, status) VALUES
('daily_check_in', '每日打卡', 10, '每日完成打卡获得的积分', 1),
('continuous_streak_7', '连续7天打卡', 50, '连续打卡7天获得的奖励积分', 1),
('continuous_streak_15', '连续15天打卡', 100, '连续打卡15天获得的奖励积分', 1),
('continuous_streak_30', '连续30天打卡', 200, '连续打卡30天获得的奖励积分', 1),
('goal_complete', '完成目标', 20, '完成健康目标获得的积分', 1);

-- 注意：用户账号会在应用启动时由 DataInitializer 自动创建
-- 默认账号：
-- 管理员：admin / admin123
-- 普通用户：user / user123
