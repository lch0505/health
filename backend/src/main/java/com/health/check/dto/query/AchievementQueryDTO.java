package com.health.check.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AchievementQueryDTO extends PageQueryDTO {

    private String name;

    private Integer status;
}
