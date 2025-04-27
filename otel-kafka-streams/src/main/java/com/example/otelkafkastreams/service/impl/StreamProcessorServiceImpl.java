package com.example.otelkafkastreams.service.impl;

import com.example.otelkafkastreams.model.LogEntity;
import com.example.otelkafkastreams.processor.FlatProcessor;
import com.example.otelkafkastreams.processor.LogRecordTransformer;
import com.example.otelkafkastreams.processor.LogSinkConnector;
import com.example.otelkafkastreams.serde.LogEntityListSerde;
import com.example.otelkafkastreams.service.StreamProcessorService;
import io.opentelemetry.proto.logs.v1.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Kafka流处理服务，负责处理流数据的转换和持久化
 */
@Slf4j
@Service
public class StreamProcessorServiceImpl implements StreamProcessorService {

    private final FlatProcessor flatProcessor;
    private final LogRecordTransformer transformer;
    private final LogSinkConnector logSinkConnector;
    
    @Value("${application.window.size-seconds:60}")
    private int windowSizeSeconds;
    
    @Value("${application.window.max-records:1000}")
    private int windowMaxRecords;

    public StreamProcessorServiceImpl(FlatProcessor flatProcessor,
                                      LogRecordTransformer transformer,
                                      LogSinkConnector logSinkConnector) {
        this.flatProcessor = flatProcessor;
        this.transformer = transformer;
        this.logSinkConnector = logSinkConnector;
    }

    /**
     * 处理输入流，使用时间窗口和消息计数双重触发条件进行批处理
     * @param inputStream 输入的Kafka流
     * @return 处理后的流
     */
    @Override
    public KStream<String, byte[]> processStream(KStream<String, byte[]> inputStream) {
        // 对数据流进行转换处理
        inputStream
            .peek((k, v) -> log.info("Stream received raw data: key={}, value.length={}", k, v.length))
            // 1. 扁平化处理消息，将一条消息转换为多条LogRecord
            .flatMap((key, value) -> {
                log.info("Processing Kafka message with length: {}", value.length);
                List<KeyValue<String, LogRecord>> result = new ArrayList<>();
                try {
                    // 解析并展开数据
                    String finalKey = UUID.randomUUID().toString();
                    for (LogRecord logRecord : flatProcessor.fanOut(value)) {
                        result.add(KeyValue.pair(finalKey, logRecord));
                    }
                } catch (Exception e) {
                    log.error("Error processing message: {}", e.getMessage(), e);
                }
                return result;
            })
            // 2. 转换为LogEntity对象
            .mapValues(value -> {
                LogEntity logEntity = null;
                try {
                    logEntity = transformer.transform(value);
                } catch (Exception e) {
                    log.error("Error transforming message: {}", e.getMessage(), e);
                }
                return logEntity;
            })
            // 2.1 过滤掉所有 value == null 的记录
            .filter((key, logEntity) -> {
                if (logEntity == null) {
                    log.warn("Skipping null LogEntity for key {}", key);
                    return false;
                }
                return true;
            })
            // 3. 分组并应用窗口函数
            .groupByKey(
                Grouped.with(
                        Serdes.String(),               // key serde
                        new JsonSerde<>(LogEntity.class) // value serde
                )
            )
            // 时间窗口设置
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(windowSizeSeconds)))
            // 在窗口内聚合消息
            .aggregate(
                ArrayList::new,  // 初始化一个空数组列表
                (key, value, aggregate) -> {
                    aggregate.add(value);
                    // 如果达到了消息数量阈值，触发处理并清空列表
                    if (aggregate.size() >= windowMaxRecords) {
                        log.info("Message count threshold reached: {} records. Triggering batch processing.", aggregate.size());
                        logSinkConnector.processBatch(aggregate);
                        return new ArrayList<>();
                    }
                    return aggregate;
                },
                Materialized
                        .<String, List<LogEntity>>as(Stores.inMemoryWindowStore(
                                "in-memory-log-store",              // store名字
                                        Duration.ofMinutes(10),                   // retention时间（窗口+留存buffer）
                                        Duration.ofSeconds(windowSizeSeconds),    // 窗口大小
                                        false                                      // 是否保持每个key多版本（用不到）
                        ))
                        .withKeySerde(Serdes.String())
                        .withValueSerde(new LogEntityListSerde())
                        .withLoggingDisabled()
            )
            // 4. 当窗口关闭时执行处理
            .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
            .toStream()
            // 5. 处理最终结果
            .foreach((windowedKey,
                      aggregate) -> {
                log.info("Time window closed for key: {}. Processing {} records.",
                        windowedKey.key(), aggregate.size());
                try {
                    logSinkConnector.processBatch(aggregate);
                } catch (Exception e) {
                    log.error("Error processing message: {}", e.getMessage(), e);
                }
            });
        
        return inputStream;
    }
    

} 