package com.example.otel.observaflow.mapper;

import com.example.otel.observaflow.model.LogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LogMapper {
    /**
     * 批量插入日志记录
     * @param logs 日志列表
     * @param tableName 目标表名
     */
    void batchInsert(@Param("list") List<LogEntity> logs, @Param("tableName") String tableName);
}
