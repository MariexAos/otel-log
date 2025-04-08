package com.example.otelkafkastreams.processor;

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

    public LogEntity transform(LogRecord log) {
        // 1. timestamp 纳秒转 LocalDateTime（UTC 可改为 Asia/Shanghai）
        long timeNano = log.getTimeUnixNano();
        Instant instant = Instant.ofEpochSecond(timeNano / 1_000_000_000L, timeNano % 1_000_000_000L);
        LocalDateTime timestamp = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Shanghai"));

        // 2. body 内容（string 类型）
        String body = log.hasBody() ? log.getBody().getStringValue() : null;

        // 3. severity
        String severity = log.getSeverityText();

        // 4. trace/spanId
        String traceId = log.getTraceId().isEmpty() ? null : bytesToHex(log.getTraceId().toByteArray());
        String spanId = log.getSpanId().isEmpty() ? null : bytesToHex(log.getSpanId().toByteArray());

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
                .build();
    }

    // traceId / spanId 为 byte[]，转换为 hex string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
