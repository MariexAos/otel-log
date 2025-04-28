package com.example.otel.observaflow.service;

import com.example.otel.observaflow.model.LogEntity;

import java.util.List;

public interface LogStoreService {

    /**
     * 直接处理日志批次，立即写入数据库
     */
    void processBatch(List<LogEntity> logs);
}
