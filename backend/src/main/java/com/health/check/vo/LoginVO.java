package com.health.check.vo;

import lombok.Data;

/**
 * 登录返回VO
 */
@Data
public class LoginVO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 角色
     */
    private String role;

    /**
     * JWT令牌
     */
    private String token;
}
