package com.hzbankwealth.observa.api.service;

import com.hzbankwealth.observa.api.model.logging.Histogram;

import java.time.LocalDateTime;

public interface LogHistogramService {
    
    Histogram getHistogram(
            String namespaces, String namespaceQuery,
            String pods, String podQuery,
            String containers, String containerQuery,
            String logQuery,
            LocalDateTime startTime, LocalDateTime endTime,
            String interval, String cluster);
} 