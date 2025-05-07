package com.hzbankwealth.observa.api.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("otel_logs_test")
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
    /**
     * 集群名称
     */
    private String cluster;
    /**
     * 命名空间
     */
    private String namespace;
    /**
     * pod名称
     */
    private String pod;
    /**
     * 容器名称
     */
    private String container;
}