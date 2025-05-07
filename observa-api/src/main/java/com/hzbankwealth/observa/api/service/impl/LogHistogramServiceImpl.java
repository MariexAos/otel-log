package com.hzbankwealth.observa.api.service.impl;

import com.hzbankwealth.observa.api.model.logging.Bucket;
import com.hzbankwealth.observa.api.model.logging.Histogram;
import com.hzbankwealth.observa.api.service.LogHistogramService;
import com.hzbankwealth.observa.api.util.QueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogHistogramServiceImpl implements LogHistogramService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Histogram getHistogram(
            String namespaces, String namespaceQuery,
            String pods, String podQuery,
            String containers, String containerQuery,
            String logQuery,
            String startTime, String endTime,
            String interval, String cluster) {
        
        // 解析时间间隔
        int intervalValue = 15;
        String intervalUnit = "m";
        
        if (StringUtils.hasText(interval)) {
            var matcher = QueryUtil.INTERVAL_PATTERN.matcher(interval);
            if (matcher.matches()) {
                intervalValue = Integer.parseInt(matcher.group(1));
                intervalUnit = matcher.group(2);
            }
        }
        
        // 转换为SQL间隔表达式
        String sqlIntervalExpr = switch (intervalUnit) {
            case "s" -> intervalValue + " SECOND";
            case "m" -> intervalValue + " MINUTE";
            case "h" -> intervalValue + " HOUR";
            case "d" -> intervalValue + " DAY";
            case "w" -> (intervalValue * 7) + " DAY";
            case "M" -> intervalValue + " MONTH";
            case "q" -> (intervalValue * 3) + " MONTH";
            case "y" -> intervalValue + " YEAR";
            default -> "15 MINUTE";
        };
        
        // 解析时间范围
        LocalDateTime startDateTime = QueryUtil.parseTimestamp(startTime);
        LocalDateTime endDateTime = QueryUtil.parseTimestamp(endTime);
        
        // 构建直方图查询
        StringBuilder histogramSql = new StringBuilder(
                "SELECT UNIX_TIMESTAMP(time_bucket) as time_bucket, COUNT(*) as count FROM (" +
                "  SELECT DATE_TRUNC('" + sqlIntervalExpr.split(" ")[1] + "', timestamp) as time_bucket " +
                "  FROM otel_logs_test WHERE 1=1");
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        
        // 添加过滤条件
        StringBuilder dummySql = new StringBuilder(); // 未使用，仅为复用applyFilters方法
        QueryUtil.applyFilters(dummySql, histogramSql, params,
                namespaces, namespaceQuery,
                pods, podQuery, containers, containerQuery, 
                logQuery, startTime, endTime, cluster);
        
        histogramSql.append(") AS t GROUP BY time_bucket ORDER BY time_bucket");
        
        // 执行查询
        List<Bucket> buckets = namedParameterJdbcTemplate.query(histogramSql.toString(), params, (rs, rowNum) -> {
            return Bucket.builder()
                    .time(rs.getLong("time_bucket"))
                    .count(rs.getLong("count"))
                    .build();
        });
        
        // 计算总数
        long total = buckets.stream().mapToLong(Bucket::getCount).sum();
        
        return Histogram.builder()
                .total(total)
                .histograms(buckets)
                .build();
    }
} 