package com.health.check.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MoodRecordDTO {

    @NotBlank(message = "心情类型不能为空")
    private String moodType;

    private String remark;
}
