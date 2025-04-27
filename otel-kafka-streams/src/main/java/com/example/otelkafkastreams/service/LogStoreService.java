package com.example.otelkafkastreams.service;

import com.example.otelkafkastreams.model.LogEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LogStoreService {

    /**
     * 直接处理日志批次，立即写入数据库
     */
    @Transactional
    void processBatch(List<LogEntity> logs);
}
