package com.example.otel.observaflow.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "otel.log")
public class OtelLogProperties {
    /**
     * 映射字段，例如：
     * cluster → k8s.cluster.name
     * podName → k8s.pod.name
     */
    private ExtractAttributes extractAttributes;

    @Data
    public static class ExtractAttributes {
        private String cluster;
        private String namespace;
        private String pod;
        private String container;
    }
}
