package com.hzbankwealth.observa.api.controller;

import cn.hutool.core.util.StrUtil;
import com.hzbankwealth.observa.api.service.LogExportService;
import com.hzbankwealth.observa.api.service.LogHistogramService;
import com.hzbankwealth.observa.api.service.LogQueryService;
import com.hzbankwealth.observa.api.service.LogStatisticsService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LogController {

    private final LogQueryService logQueryService;
    private final LogStatisticsService logStatisticsService;
    private final LogHistogramService logHistogramService;
    private final LogExportService logExportService;

    @GetMapping("/observa-api/logging/v1/logs")
    public ResponseEntity<?> queryLogs(
            @RequestParam(value = "operation", defaultValue = "query") String operation,
            @RequestParam(value = "namespaces", required = false) String namespaces,
            @RequestParam(value = "namespace_query", required = false) String namespaceQuery,
            @RequestParam(value = "pods", required = false) String pods,
            @RequestParam(value = "pod_query", required = false) String podQuery,
            @RequestParam(value = "containers", required = false) String containers,
            @RequestParam(value = "container_query", required = false) String containerQuery,
            @RequestParam(value = "log_query", required = false) String logQuery,
            @RequestParam(value = "interval", required = false) String interval,
            @Parameter(
                    description = "开始时间，格式为 yyyy-MM-dd'T'HH:mm:ss，例如：2025-05-01T08:00:00"
            )
            @RequestParam(value = "start_time", required = false) String startTime,
            @Parameter(
                    description = "结束时间，格式为 yyyy-MM-dd'T'HH:mm:ss，例如：2025-05-01T08:00:00"
            )
            @RequestParam(value = "end_time", required = false) String endTime,
            @RequestParam(value = "sort", defaultValue = "desc", required = false) String sort,
            @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(value = "cluster", required = false) String cluster) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        LocalDateTime startLocalTime = StrUtil.isNotBlank(startTime)
                ? LocalDateTime.parse(startTime, formatter)
                : null; // 默认值

        LocalDateTime endLocalTime = StrUtil.isNotBlank(endTime)
                ? LocalDateTime.parse(endTime, formatter)
                : LocalDateTime.now();

        return switch (operation.toLowerCase()) {
            case "query" -> ResponseEntity.ok(logQueryService.queryLogs(
                    namespaces, namespaceQuery, 
                    pods, podQuery, containers, containerQuery, logQuery,
                    startLocalTime, endLocalTime, sort, from, size, cluster));
            case "statistics" -> ResponseEntity.ok(logStatisticsService.getStatistics(
                    namespaces, namespaceQuery, 
                    pods, podQuery, containers, containerQuery, logQuery,
                    startTime, endTime, cluster));
            case "histogram" -> ResponseEntity.ok(logHistogramService.getHistogram(
                    namespaces, namespaceQuery, 
                    pods, podQuery, containers, containerQuery, logQuery,
                    startLocalTime, endLocalTime, interval, cluster));
            case "export" -> ResponseEntity.ok(logExportService.exportLogs(
                    namespaces, namespaceQuery, 
                    pods, podQuery, containers, containerQuery, logQuery,
                    startTime, endTime, sort, cluster));
            default -> ResponseEntity.badRequest().body(Map.of("error", "Unsupported operation type"));
        };
    }
} 