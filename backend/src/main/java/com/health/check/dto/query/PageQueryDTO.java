package com.health.check.dto.query;

import com.health.check.common.constants.PageConstants;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页查询基础DTO
 */
@Data
public class PageQueryDTO {

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer page = PageConstants.DEFAULT_PAGE;

    /**
     * 每页条数
     */
    @Min(value = 1, message = "每页条数必须大于等于1")
    @Max(value = 100, message = "每页条数不能超过100")
    private Integer size = PageConstants.DEFAULT_SIZE;

    public Integer getPage() {
        return page == null ? PageConstants.DEFAULT_PAGE : page;
    }

    public Integer getSize() {
        return size == null ? PageConstants.DEFAULT_SIZE : size;
    }
}
