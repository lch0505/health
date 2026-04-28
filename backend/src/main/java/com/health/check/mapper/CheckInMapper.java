package com.health.check.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.check.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {
}
