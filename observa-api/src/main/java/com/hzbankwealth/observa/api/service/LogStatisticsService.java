package com.hzbankwealth.observa.api.service;

import com.hzbankwealth.observa.api.model.logging.Statistics;

public interface LogStatisticsService {
    
    Statistics getStatistics(
            String namespaces, String namespaceQuery,
            String pods, String podQuery,
            String containers, String containerQuery,
            String logQuery,
            String startTime, String endTime,
            String cluster);
} 