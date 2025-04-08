package com.example.otelkafkastreams.config;

import com.example.otelkafkastreams.processor.FlatProcessor;
import com.example.otelkafkastreams.processor.LogRecordTransformer;
import com.example.otelkafkastreams.processor.LogSinkConnector;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.util.Collections;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {
    private static final Logger log = LoggerFactory.getLogger(KafkaStreamsConfig.class);

    @Value("${application.kafka.input-topic}")
    private String inputTopic;

    private final LogSinkConnector logSinkConnector;

    private final FlatProcessor flatProcessor;
    private final LogRecordTransformer transformer;


    public KafkaStreamsConfig(LogSinkConnector logSinkConnector, FlatProcessor flatProcessor, LogRecordTransformer transformer) {
        this.logSinkConnector = logSinkConnector;
        this.flatProcessor = flatProcessor;
        this.transformer = transformer;
    }

    @Bean
    public KStream<String, byte[]> kStream(StreamsBuilder streamsBuilder) {
        // consume data from kafka topic
        KStream<String, byte[]> stream = streamsBuilder.stream(
                inputTopic,
                Consumed.with(Serdes.String(), Serdes.ByteArray())
        );
        log.info("Kafka Stream listening on topic: {}", inputTopic);

        // process data
        stream
            .peek((k, v) -> log.debug("Stream received raw data: key={}, value.length={}", k, v.length))
            .flatMapValues(value -> {
                    log.debug("Received Kafka message: {}", value.length);
                    // fanout data
                    try {
                        return flatProcessor.fanOut(value);
                    } catch (Exception e) {
                        log.error("Error processing message: {}", e.getMessage(), e);
                        return Collections.emptyList();
                    }
                })
                .mapValues(transformer::transform)
                .foreach((k, v) -> {
                    log.info("log record: {}", v);
                    logSinkConnector.buffer(v);
                });
        return stream;
    }
} 