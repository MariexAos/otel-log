package com.example.otelkafkastreams.processor;

import com.example.otelkafkastreams.model.LogEntity;
import com.example.otelkafkastreams.service.LogStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 日志服务，负责解析和处理OpenTelemetry日志
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogSinkConnector {
    
    private final LogStoreService directLogService;

    /**
     * 处理多个日志，直接写入数据库
     */
    public void processBatch(List<LogEntity> logs) {
        directLogService.processBatch(logs);
    }
}
