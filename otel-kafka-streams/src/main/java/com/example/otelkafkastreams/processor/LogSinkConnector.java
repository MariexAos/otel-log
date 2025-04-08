package com.example.otelkafkastreams.processor;

import com.example.otelkafkastreams.mapper.LogMapper;
import com.example.otelkafkastreams.model.LogEntity;
import com.example.otelkafkastreams.service.BufferService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 日志服务，负责解析和处理OpenTelemetry日志
 */
@Slf4j
@Service
public class LogSinkConnector {
    
    private final LogMapper logMapper;
    private final BufferService<LogEntity> bufferService;
    
    @Value("${application.doris.table-name}")
    private String tableName;
    
    @Autowired
    public LogSinkConnector(
            LogMapper logMapper,
            BufferService<LogEntity> bufferService) {
        this.logMapper = logMapper;
        this.bufferService = bufferService;
        
        // 设置缓冲区刷新处理器
        this.bufferService.setFlushProcessor(this::processLogBatch);
    }
    
    @PostConstruct
    public void init() {
        log.info("Log service initialized with table name: {}", tableName);
    }

    /**
     * 添加单个日志到缓冲区
     */
    public void buffer(LogEntity logEntity) {
        bufferService.buffer(logEntity);
    }
    
    /**
     * 添加多个日志到缓冲区
     */
    public void bufferAll(List<LogEntity> logs) {
        bufferService.bufferAll(logs);
    }
    
    /**
     * 立即刷新缓冲区
     */
    public void flushBuffer() {
        bufferService.flush();
    }
    
    /**
     * 处理日志批次，将其插入数据库
     * 作为BufferService的刷新处理器使用
     */
    @Transactional
    public void processLogBatch(List<LogEntity> logs) {
        if (logs == null || logs.isEmpty()) {
            return;
        }
        
        try {
            // 使用MyBatis mapper进行批量插入
            logMapper.batchInsert(logs, tableName);
            log.debug("Successfully inserted {} logs using MyBatis", logs.size());
        } catch (Exception e) {
            log.error("Failed to insert logs using MyBatis: {}", e.getMessage(), e);
            throw e;  // 重新抛出异常以触发事务回滚
        }
    }
}
