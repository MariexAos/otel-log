package com.example.otel.observaflow.mapper;

import com.example.otel.observaflow.model.LogEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class LogMapperTest {
    @Autowired
    LogMapper logMapper;

    @Test
    void batchInsertTest() {
        List<LogEntity> logs = new ArrayList<>();
        LogEntity log1 = new LogEntity();
        log1.setTimestamp(LocalDateTime.now().minusSeconds(30));
        log1.setBody("User login success");
        log1.setSeverity("INFO");
        log1.setTraceId("trace-001");
        log1.setSpanId("span-001");
        log1.setAttributes("{\"userId\":\"123\",\"ip\":\"10.0.0.1\"}");

        LogEntity log2 = new LogEntity();
        log2.setTimestamp(LocalDateTime.now().minusSeconds(10));
        log2.setBody("DB query timeout");
        log2.setSeverity("ERROR");
        log2.setTraceId("trace-002");
        log2.setSpanId("span-002");
        log2.setAttributes("{\"query\":\"SELECT * FROM users\",\"duration\":\"5s\"}");

        logs.add(log1);
        logs.add(log2);
        logMapper.batchInsert(logs, "observa_otel_logs");
    }
}