package com.example.otelkafkastreams.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntity {
    /**
     * 日期转换
     */
    private LocalDateTime timestamp;
    /**
     * 日志内容
     */
    private String body;
    /**
     * 严重级别
     */
    private String severity;
    /**
     * 链路ID
     */
    private String traceId;
    /**
     * 跨度ID
     */
    private String spanId;
    /**
     * json 字符串
     */
    private String attributes;
} 