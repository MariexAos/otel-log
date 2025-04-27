package com.example.otelkafkastreams.processor;

import com.example.otelkafkastreams.properties.OtelLogProperties;
import com.example.otelkafkastreams.model.LogEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.logs.v1.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class LogRecordTransformer {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final OtelLogProperties otelLogProperties;

    public LogRecordTransformer(OtelLogProperties otelLogProperties) {
        this.otelLogProperties = otelLogProperties;
    }

    public LogEntity transform(LogRecord log) {
        // 1. timestamp 纳秒转 LocalDateTime（使用系统默认时区）
        long timeNano = log.getTimeUnixNano();
        LocalDateTime timestamp = Instant
                .ofEpochSecond(0, timeNano)   // 纳秒偏移一次性传入
                .atZone(ZoneId.systemDefault())           // 使用系统默认时区
                .toLocalDateTime();

        // 2. body 内容（string 类型）
        String body = log.hasBody() ? log.getBody().getStringValue() : null;

        // 3. severity
        String severity = log.getSeverityText();

        // 4. trace/spanId
        String traceId = log.getTraceId().isEmpty() ? null : log.getTraceId().toStringUtf8();
        String spanId = log.getSpanId().isEmpty() ? null : log.getSpanId().toStringUtf8();

        // 5. attributes → Map<String, Object> → JSON
        Map<String, Object> attrMap = new HashMap<>();
        for (KeyValue kv : log.getAttributesList()) {
            String key = kv.getKey();
            if (kv.hasValue()) {
                attrMap.put(key,kv.getValue().toString());
            }
        }
        String attributesJson = "{}";
        try {
            attributesJson = objectMapper.writeValueAsString(attrMap);
        } catch (Exception ignored) {

        }

        return LogEntity.builder()
                .timestamp(timestamp)
                .body(body)
                .severity(severity)
                .traceId(traceId)
                .spanId(spanId)
                .attributes(attributesJson)
                .cluster(attrMap.getOrDefault(otelLogProperties.getExtractAttributes().getCluster(), "").toString())
                .namespace(attrMap.getOrDefault(otelLogProperties.getExtractAttributes().getNamespace(), "").toString())
                .pod(attrMap.getOrDefault(otelLogProperties.getExtractAttributes().getPod(), "").toString())
                .container(attrMap.getOrDefault(otelLogProperties.getExtractAttributes().getContainer(), "").toString())
                .build();
    }
}
