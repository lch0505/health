package com.health.check.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.check.entity.MoodRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MoodRecordMapper extends BaseMapper<MoodRecord> {
}
