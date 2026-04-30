package com.health.check.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 更新状态请求DTO
 */
@Data
public class UpdateStatusDTO {

    /**
     * 状态值
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
