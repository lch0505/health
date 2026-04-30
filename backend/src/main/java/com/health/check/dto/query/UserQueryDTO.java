package com.health.check.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageQueryDTO {

    /**
     * 用户名（支持模糊查询）
     */
    private String username;

    /**
     * 角色
     */
    private String role;
}
