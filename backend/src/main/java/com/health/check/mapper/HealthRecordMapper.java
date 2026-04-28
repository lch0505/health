package com.health.check.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.check.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {
}
