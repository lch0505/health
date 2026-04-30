package com.health.check.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class PointsRecordQueryDTO extends PageQueryDTO {

    private Long userId;

    private LocalDate startDate;

    private LocalDate endDate;

    private String pointsType;
}
