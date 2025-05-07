package com.hzbankwealth.observa.api.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class QueryUtil {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    public static final Pattern INTERVAL_PATTERN = Pattern.compile("^(\\d+)([smhdwMqy])$");

    /**
     * 应用查询过滤条件到SQL语句
     */
    public static void applyFilters(
            StringBuilder sql1, StringBuilder sql2, MapSqlParameterSource params,
            String namespaces, String namespaceQuery,
            String pods, String podQuery,
            String containers, String containerQuery,
            String logQuery, String startTime, String endTime, String cluster) {

        // 集群过滤
        if (StringUtils.hasText(cluster)) {
            sql1.append(" AND cluster = :cluster");
            sql2.append(" AND cluster = :cluster");
            params.addValue("cluster", cluster);
        }

        // 命名空间过滤
        List<String> namespaceList = splitAndTrim(namespaces);
        if (!namespaceList.isEmpty()) {
            sql1.append(" AND namespace IN (:namespaces)");
            sql2.append(" AND namespace IN (:namespaces)");
            params.addValue("namespaces", namespaceList);
        } else if (StringUtils.hasText(namespaceQuery)) {
            sql1.append(" AND namespace LIKE :namespaceQuery");
            sql2.append(" AND namespace LIKE :namespaceQuery");
            params.addValue("namespaceQuery", "%" + namespaceQuery + "%");
        }

        // Pod过滤
        List<String> podList = splitAndTrim(pods);
        if (!podList.isEmpty()) {
            sql1.append(" AND pod IN (:pods)");
            sql2.append(" AND pod IN (:pods)");
            params.addValue("pods", podList);
        } else if (StringUtils.hasText(podQuery)) {
            sql1.append(" AND pod LIKE :podQuery");
            sql2.append(" AND pod LIKE :podQuery");
            params.addValue("podQuery", "%" + podQuery + "%");
        }

        // 容器过滤
        List<String> containerList = splitAndTrim(containers);
        if (!containerList.isEmpty()) {
            sql1.append(" AND container IN (:containers)");
            sql2.append(" AND container IN (:containers)");
            params.addValue("containers", containerList);
        } else if (StringUtils.hasText(containerQuery)) {
            sql1.append(" AND container LIKE :containerQuery");
            sql2.append(" AND container LIKE :containerQuery");
            params.addValue("containerQuery", "%" + containerQuery + "%");
        }

        // 日志内容过滤
        if (StringUtils.hasText(logQuery)) {
            sql1.append(" AND body LIKE :logQuery");
            sql2.append(" AND body LIKE :logQuery");
            params.addValue("logQuery", "%" + logQuery + "%");
        }

        // 时间范围过滤
        LocalDateTime startDateTime = parseTimestamp(startTime);
        if (startDateTime != null) {
            sql1.append(" AND timestamp >= :startTime");
            sql2.append(" AND timestamp >= :startTime");
            params.addValue("startTime", startDateTime);
        }
        
        LocalDateTime endDateTime = parseTimestamp(endTime);
        if (endDateTime != null) {
            sql1.append(" AND timestamp <= :endTime");
            sql2.append(" AND timestamp <= :endTime");
            params.addValue("endTime", endDateTime);
        }
    }
    
    /**
     * 解析Unix时间戳字符串为LocalDateTime
     */
    public static LocalDateTime parseTimestamp(String timestampStr) {
        if (!StringUtils.hasText(timestampStr)) {
            return null;
        }
        
        try {
            long timestamp = Long.parseLong(timestampStr);
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        } catch (NumberFormatException e) {
            log.warn("Invalid timestamp format: {}", timestampStr);
            return null;
        }
    }
    
    /**
     * 拆分并整理逗号分隔的字符串
     */
    public static List<String> splitAndTrim(String input) {
        if (!StringUtils.hasText(input)) {
            return List.of();
        }
        
        List<String> result = new ArrayList<>();
        for (String part : input.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        
        return result;
    }
} 