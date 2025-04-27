package com.example.otelkafkastreams.serde;

import com.example.otelkafkastreams.model.LogEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用于LogEntity列表的序列化和反序列化
 */
public class LogEntityListSerde implements Serde<List<LogEntity>> {
    private static final Logger log = LoggerFactory.getLogger(LogEntityListSerde.class);
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()); // 处理Java 8日期时间类型

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // 不需要配置
    }

    @Override
    public void close() {
        // 不需要关闭资源
    }

    @Override
    public Serializer<List<LogEntity>> serializer() {
        return new Serializer<>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
                // 不需要配置
            }

            @Override
            public byte[] serialize(String topic, List<LogEntity> data) {
                if (data == null) {
                    return null;
                }
                try {
                    return mapper.writeValueAsBytes(data);
                } catch (JsonProcessingException e) {
                    log.error("Error serializing LogEntity list: {}", e.getMessage(), e);
                    return new byte[0];
                }
            }

            @Override
            public void close() {
                // 不需要关闭资源
            }
        };
    }

    @Override
    public Deserializer<List<LogEntity>> deserializer() {
        return new Deserializer<>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
                // 不需要配置
            }

            @Override
            public List<LogEntity> deserialize(String topic, byte[] data) {
                if (data == null || data.length == 0) {
                    return new ArrayList<>();
                }
                try {
                    return mapper.readValue(data, new TypeReference<List<LogEntity>>() {});
                } catch (IOException e) {
                    log.error("Error deserializing LogEntity list: {}", e.getMessage(), e);
                    return new ArrayList<>();
                }
            }

            @Override
            public void close() {
                // 不需要关闭资源
            }
        };
    }
} 