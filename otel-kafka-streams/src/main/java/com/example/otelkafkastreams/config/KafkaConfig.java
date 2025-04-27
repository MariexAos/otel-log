package com.example.otelkafkastreams.config;

import com.example.otelkafkastreams.service.StreamProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;

@Configuration
@EnableKafkaStreams
@Slf4j
public class KafkaConfig {

    @Value("${application.kafka.input-topic}")
    private String inputTopic;

    private final StreamProcessorService streamProcessorService;

    public KafkaConfig(StreamProcessorService streamProcessorService) {
        this.streamProcessorService = streamProcessorService;
    }

    private static void configure(StreamsBuilderFactoryBean fb) {
        fb.setStateListener((newState, oldState) -> {
            log.info("State transition from {} to {}", oldState, newState);
        });
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer configurer() {
        return KafkaConfig::configure;
    }
    @Bean
    public KStream<String, byte[]> kStream(StreamsBuilder streamsBuilder) {
        // 从Kafka主题获取流
        KStream<String, byte[]> stream = streamsBuilder.stream(
                inputTopic,
                Consumed.with(Serdes.String(), Serdes.ByteArray())
        );
        log.debug("Kafka Stream listening on topic: {}", inputTopic);
        return streamProcessorService.processStream(stream);
    }
} 