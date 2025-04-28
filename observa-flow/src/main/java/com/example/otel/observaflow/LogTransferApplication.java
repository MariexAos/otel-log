package com.example.otel.observaflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
public class LogTransferApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogTransferApplication.class, args);
    }
} 