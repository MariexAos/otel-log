package com.example.otelkafkastreams.service.impl;

import com.example.otelkafkastreams.config.LogBufferProperties;
import com.example.otelkafkastreams.model.LogEntity;
import com.example.otelkafkastreams.service.BufferService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * 日志缓冲服务实现，管理日志的内存缓冲、持久化和定时刷新
 */
@Slf4j
@Service
public class LogBufferServiceImpl implements BufferService<LogEntity> {
    
    private final BlockingQueue<LogEntity> buffer;
    private final AtomicBoolean isFlushingBuffer = new AtomicBoolean(false);
    private final ObjectMapper objectMapper;
    private final LogBufferProperties bufferProperties;
    
    @Value("${application.doris.batch-size:5000}")
    private int batchSize;
    
    private Consumer<List<LogEntity>> flushProcessor;
    
    @Autowired
    public LogBufferServiceImpl(ObjectMapper objectMapper, LogBufferProperties bufferProperties) {
        this.objectMapper = objectMapper;
        this.bufferProperties = bufferProperties;
        this.buffer = new LinkedBlockingQueue<>();
        ensurePersistenceDirectoryExists();
    }
    
    @PostConstruct
    public void init() {
        log.info("LogBufferService initialized with persistence file: {}, persistence enabled: {}", 
                bufferProperties.getPersistenceFile(), bufferProperties.isPersistenceEnabled());
        
        // 启动时恢复持久化的日志
        if (bufferProperties.isPersistenceEnabled()) {
            recoverPersistedLogs();
        }
    }
    
    @Override
    public void buffer(LogEntity item) {
        buffer.add(item);
        
        // 如果缓冲区大小超过阈值，触发刷新
        if (buffer.size() >= batchSize) {
            flush();
        }
    }
    
    @Override
    public void bufferAll(List<LogEntity> items) {
        buffer.addAll(items);
        
        // 如果缓冲区大小超过阈值，触发刷新
        if (buffer.size() >= batchSize) {
            flush();
        }
    }
    
    /**
     * 定时任务，根据时间间隔定期刷新缓冲区
     */
    @Scheduled(fixedDelayString = "${application.doris.batch-interval-ms:1000}")
    public void scheduledFlush() {
        if (!buffer.isEmpty()) {
            flush();
        }
    }
    
    @Override
    public synchronized void flush() {
        // 如果已经在刷新中，直接返回
        if (isFlushingBuffer.getAndSet(true)) {
            return;
        }
        
        try {
            List<LogEntity> items = new ArrayList<>();
            buffer.drainTo(items, batchSize);
            
            if (!items.isEmpty()) {
                // 首先持久化到文件作为备份
                if (bufferProperties.isPersistenceEnabled()) {
                    persistItemsToFile(items);
                }
                
                // 然后调用处理器进行处理
                if (flushProcessor != null) {
                    try {
                        flushProcessor.accept(items);
                        
                        // 处理成功后删除持久化文件
                        if (bufferProperties.isPersistenceEnabled()) {
                            deletePersistenceFile();
                        }
                        
                        log.info("Flushed {} items from buffer", items.size());
                    } catch (Exception e) {
                        log.error("Error processing flushed items: {}", e.getMessage(), e);
                        throw e; // 重新抛出以触发回滚
                    }
                } else {
                    log.warn("No flush processor set, items will only be persisted to file");
                }
            }
        } catch (Exception e) {
            log.error("Error flushing buffer: {}", e.getMessage(), e);
            // 出错时确保所有日志都被持久化
            if (bufferProperties.isPersistenceEnabled()) {
                try {
                    persistAllItemsToFile();
                } catch (Exception ex) {
                    log.error("Failed to persist items during error recovery", ex);
                }
            }
        } finally {
            isFlushingBuffer.set(false);
        }
    }
    
    @Override
    public void setFlushProcessor(Consumer<List<LogEntity>> processor) {
        this.flushProcessor = processor;
    }
    
    /**
     * 将项目持久化到文件以便恢复
     */
    private void persistItemsToFile(List<LogEntity> items) {
        try {
            objectMapper.writeValue(new File(bufferProperties.getPersistenceFile()), items);
        } catch (IOException e) {
            log.error("Failed to persist items to file", e);
            throw new RuntimeException("Failed to persist items", e);
        }
    }
    
    /**
     * 将所有缓冲区中的项目持久化到文件
     */
    private void persistAllItemsToFile() {
        List<LogEntity> allItems = new ArrayList<>(buffer);
        try {
            objectMapper.writeValue(new File(bufferProperties.getPersistenceFile()), allItems);
            log.info("Persisted {} items to file for recovery", allItems.size());
        } catch (IOException e) {
            log.error("Failed to persist all items to file", e);
        }
    }
    
    /**
     * 启动时从文件恢复持久化的项目
     */
    private void recoverPersistedLogs() {
        File file = new File(bufferProperties.getPersistenceFile());
        if (file.exists()) {
            try {
                List<LogEntity> recoveredItems = objectMapper.readValue(
                    file, new TypeReference<List<LogEntity>>() {});
                
                if (!recoveredItems.isEmpty()) {
                    // 添加恢复的项目回到缓冲区
                    buffer.addAll(recoveredItems);
                    log.info("Recovered {} items from persistence file", recoveredItems.size());
                    
                    // 如果处理器已设置，安排立即刷新
                    if (flushProcessor != null) {
                        flush();
                    }
                }
            } catch (IOException e) {
                log.error("Failed to recover items from persistence file", e);
            }
        }
    }
    
    /**
     * 成功处理后删除持久化文件
     */
    private void deletePersistenceFile() {
        try {
            Files.deleteIfExists(Paths.get(bufferProperties.getPersistenceFile()));
        } catch (IOException e) {
            log.warn("Failed to delete persistence file", e);
        }
    }
    
    /**
     * 确保持久化目录存在
     */
    private void ensurePersistenceDirectoryExists() {
        try {
            Path directory = Paths.get(bufferProperties.getPersistenceFile()).getParent();
            if (directory != null) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            log.error("Failed to create persistence directory", e);
        }
    }
    
    /**
     * 关闭前刷新所有剩余项目
     */
    @PreDestroy
    @Override
    public void shutdown() {
        log.info("Flushing remaining items before shutdown");
        flush();
    }
} 