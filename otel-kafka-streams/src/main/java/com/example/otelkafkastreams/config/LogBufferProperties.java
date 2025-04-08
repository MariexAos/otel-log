package com.example.otelkafkastreams.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 日志缓冲区相关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "application.log-buffer")
public class LogBufferProperties {
    
    /**
     * 持久化文件路径
     * 默认路径：${user.home}/log-buffer/${spring.application.name}/buffer.json
     */
    private String persistenceFile = "${user.home}/log-buffer/buffer.json";
    
    /**
     * 最大重试次数
     */
    private int maxRetryAttempts = 3;
    
    /**
     * 重试间隔（毫秒）
     */
    private long retryIntervalMs = 1000;
    
    /**
     * 是否启用持久化
     */
    private boolean persistenceEnabled = true;
} 