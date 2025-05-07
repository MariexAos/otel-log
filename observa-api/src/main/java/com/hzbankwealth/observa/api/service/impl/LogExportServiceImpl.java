package com.hzbankwealth.observa.api.service.impl;

import com.hzbankwealth.observa.api.model.logging.Record;
import com.hzbankwealth.observa.api.service.LogExportService;
import com.hzbankwealth.observa.api.util.QueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogExportServiceImpl implements LogExportService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Record> exportLogs(
            String namespaces, String namespaceQuery,
            String pods, String podQuery,
            String containers, String containerQuery,
            String logQuery,
            String startTime, String endTime,
            String sort, String cluster) {
        
        StringBuilder querySql = new StringBuilder("SELECT timestamp, body, namespace, pod, container FROM otel_logs_test WHERE 1=1");
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        
        // 添加过滤条件
        StringBuilder dummySql = new StringBuilder(); // 未使用，仅为复用applyFilters方法
        QueryUtil.applyFilters(dummySql, querySql, params,
                namespaces, namespaceQuery,
                pods, podQuery, containers, containerQuery, 
                logQuery, startTime, endTime, cluster);
        
        // 应用排序
        querySql.append(" ORDER BY timestamp ").append("desc".equalsIgnoreCase(sort) ? "DESC" : "ASC");
        
        // 执行查询
        return namedParameterJdbcTemplate.query(querySql.toString(), params, (rs, rowNum) -> {
            LocalDateTime timestamp = rs.getObject("timestamp", LocalDateTime.class);
            String formattedTime = timestamp != null ? timestamp.format(QueryUtil.TIME_FORMATTER) : "";
            
            return Record.builder()
                    .time(formattedTime)
                    .log(rs.getString("body"))
                    .namespace(rs.getString("namespace"))
                    .pod(rs.getString("pod"))
                    .container(rs.getString("container"))
                    .build();
        });
    }
} 