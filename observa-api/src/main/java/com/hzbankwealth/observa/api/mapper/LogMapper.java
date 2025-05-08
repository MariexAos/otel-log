package com.hzbankwealth.observa.api.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzbankwealth.observa.api.model.LogEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface LogMapper extends BaseMapper<LogEntity> {
    
    /**
     * 查询时间分桶统计
     *
     * @param timeFormat 时间格式
     * @param ew 查询条件
     * @return 分桶统计结果
     */
    @MapKey("time_bucket")
    List<Map<String, Object>> selectTimeBucketStats(@Param("timeFormat") String timeFormat, @Param("ew") Wrapper<LogEntity> ew);
}
