package com.hzbankwealth.observa.api.service;

import com.hzbankwealth.observa.api.model.logging.Logs;

import java.time.LocalDateTime;

public interface LogQueryService {
    
    Logs queryLogs(
            String namespaces, String namespaceQuery,
            String pods, String podQuery,
            String containers, String containerQuery,
            String logQuery,
            LocalDateTime startTime, LocalDateTime endTime,
            String sort, Integer from, Integer size,
            String cluster);
} 