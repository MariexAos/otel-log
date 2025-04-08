package com.example.otelkafkastreams.service;

import com.example.otelkafkastreams.model.LogEntity;

import java.util.List;
import java.util.function.Consumer;

/**
 * 日志缓冲服务接口，负责日志的缓冲、持久化和批量处理
 */
public interface BufferService<T> {
    
    /**
     * 将单个项目添加到缓冲区
     * @param item 要缓冲的项目
     */
    void buffer(T item);
    
    /**
     * 将多个项目添加到缓冲区
     * @param items 要缓冲的项目列表
     */
    void bufferAll(List<T> items);
    
    /**
     * 立即刷新缓冲区内容
     */
    void flush();
    
    /**
     * 设置刷新处理器，当缓冲区需要刷新时调用
     * @param processor 处理器函数，接收缓冲区中的项目列表
     */
    void setFlushProcessor(Consumer<List<T>> processor);
    
    /**
     * 关闭缓冲服务，刷新所有剩余内容
     */
    void shutdown();
} 