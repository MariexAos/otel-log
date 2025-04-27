package com.example.otelkafkastreams.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.example.otelkafkastreams.mapper.LogMapper;
import com.example.otelkafkastreams.model.LogEntity;
import com.example.otelkafkastreams.service.LogStoreService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 直接写入日志服务，无缓冲区
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DirectLogServiceImpl implements LogStoreService {

    @Value("${application.doris.table-name}")
    private String tableName;

    private final LogMapper logMapper;

    @PostConstruct
    public void init() {
        log.info("Log service initialized with table name: {}", tableName);
    }

    /**
     * 直接处理日志批次，立即写入数据库
     */
    @Transactional
    public void processBatch(List<LogEntity> logs) {
        if (CollectionUtil.isEmpty(logs)) {
            return;
        }
        
        try {
            // 使用MyBatis mapper进行批量插入
            logMapper.batchInsert(logs, tableName);
            log.debug("Successfully inserted {} logs directly", logs.size());
        } catch (Exception e) {
            log.error("Failed to insert logs directly: {}", e.getMessage(), e);
            throw e;  // 重新抛出异常以触发事务回滚
        }
    }
} 