package com.hzbankwealth.observa.api.service;

import com.hzbankwealth.observa.api.model.logging.Histogram;

public interface LogHistogramService {
    
    Histogram getHistogram(
            String namespaces, String namespaceQuery,
            String pods, String podQuery,
            String containers, String containerQuery,
            String logQuery,
            String startTime, String endTime,
            String interval, String cluster);
} 