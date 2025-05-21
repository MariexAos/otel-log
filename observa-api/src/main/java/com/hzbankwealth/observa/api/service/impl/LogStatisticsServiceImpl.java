package com.hzbankwealth.observa.api.service.impl;

import com.hzbankwealth.observa.api.model.logging.Statistics;
import com.hzbankwealth.observa.api.service.LogStatisticsService;
import com.hzbankwealth.observa.api.util.QueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogStatisticsServiceImpl implements LogStatisticsService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Statistics getStatistics(
            String namespaces, String namespaceQuery,
            String pods, String podQuery,
            String containers, String containerQuery,
            String logQuery,
            String startTime, String endTime,
            String cluster) {
        
        StringBuilder countLogsSql = new StringBuilder("SELECT COUNT(*) FROM observa_otel_logs WHERE 1=1");
        StringBuilder countContainersSql = new StringBuilder("SELECT COUNT(DISTINCT container) FROM observa_otel_logs WHERE 1=1");
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        
        // 添加过滤条件
        QueryUtil.applyFilters(countLogsSql, countContainersSql, params,
                namespaces, namespaceQuery,
                pods, podQuery, containers, containerQuery, 
                logQuery, startTime, endTime, cluster);
        
        // 获取统计数据
        Long logsCount = namedParameterJdbcTemplate.queryForObject(countLogsSql.toString(), params, Long.class);
        Long containersCount = namedParameterJdbcTemplate.queryForObject(countContainersSql.toString(), params, Long.class);
        
        return Statistics.builder()
                .logs(logsCount != null ? logsCount : 0)
                .containers(containersCount != null ? containersCount : 0)
                .build();
    }
} 