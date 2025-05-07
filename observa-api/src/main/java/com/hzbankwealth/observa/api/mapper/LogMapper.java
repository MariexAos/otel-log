package com.hzbankwealth.observa.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzbankwealth.observa.api.model.LogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogMapper extends BaseMapper<LogEntity> {
}
