package com.hzbankwealth.observa.api.service;

import com.hzbankwealth.observa.api.model.logging.Record;

import java.util.List;

public interface LogExportService {
    
    List<Record> exportLogs(
            String namespaces, String namespaceQuery,
            String pods, String podQuery,
            String containers, String containerQuery,
            String logQuery,
            String startTime, String endTime,
            String sort, String cluster);
} 