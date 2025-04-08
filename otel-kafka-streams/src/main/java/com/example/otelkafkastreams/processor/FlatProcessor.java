package com.example.otelkafkastreams.processor;

import com.google.protobuf.InvalidProtocolBufferException;
import io.opentelemetry.proto.logs.v1.LogRecord;
import io.opentelemetry.proto.logs.v1.LogsData;
import io.opentelemetry.proto.logs.v1.ResourceLogs;
import io.opentelemetry.proto.logs.v1.ScopeLogs;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlatProcessor {

    public List<LogRecord> fanOut(byte[] message) throws InvalidProtocolBufferException {
        List<LogRecord> result = new ArrayList<>();
        LogsData logsData = LogsData.parseFrom(message);
        for (ResourceLogs r : logsData.getResourceLogsList()) {
            for (ScopeLogs s : r.getScopeLogsList()) {
                // 示例：构造 fanout 出来的日志记录
                result.addAll(s.getLogRecordsList());
            }
        }
        return result;
    }
}
