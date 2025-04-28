package com.example.otel.observaflow.service;

import org.apache.kafka.streams.kstream.KStream;

/**
 * 流处理器接口，定义流处理的基本行为
 */
public interface StreamProcessorService {
    
    /**
     * 处理Kafka流
     * @param inputStream 输入流
     * @return 处理后的流
     */
    KStream<String, byte[]> processStream(KStream<String, byte[]> inputStream);
} 