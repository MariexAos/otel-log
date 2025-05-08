package com.hzbankwealth.observa.api.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hzbankwealth.observa.api.mapper.LogMapper;
import com.hzbankwealth.observa.api.model.LogEntity;
import com.hzbankwealth.observa.api.model.logging.Bucket;
import com.hzbankwealth.observa.api.model.logging.Histogram;
import com.hzbankwealth.observa.api.service.LogHistogramService;
import com.hzbankwealth.observa.api.util.QueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogHistogramServiceImpl implements LogHistogramService {

    private final LogMapper logMapper;

    private LocalDateTime[] getDefaultTimeRange(String intervalUnit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime;
        
        switch (intervalUnit) {
            case "s" -> startTime = now.minusHours(1);
            case "m" -> startTime = now.minusHours(6);
            case "d" -> startTime = now.minusDays(7);
            case "M" -> startTime = now.minusMonths(10);
            case "y" -> startTime = now.minusYears(1);
            default -> startTime = now.minusHours(24);
        }
        
        return new LocalDateTime[]{startTime, now};
    }

    @Override
    public Histogram getHistogram(
            String namespaces, String namespaceQuery,
            String pods, String podQuery,
            String containers, String containerQuery,
            String logQuery,
            LocalDateTime startTime, LocalDateTime endTime,
            String interval, String cluster) {
        
        // 解析时间间隔
        String intervalUnit = "m";
        
        if (StrUtil.isNotBlank(interval) && interval.length() > 1) {
            intervalUnit = interval.substring(interval.length() - 1);
        } 
        
        // 如果没有指定时间范围，根据时间单位设置默认范围
        if (startTime == null || endTime == null) {
            LocalDateTime[] defaultRange = getDefaultTimeRange(intervalUnit);
            if (startTime == null) {
                startTime = defaultRange[0];
            }
            if (endTime == null) {
                endTime = defaultRange[1];
            }
        }
        
        // 转换为时间格式
        String timeFormat = switch (intervalUnit) {
            case "s" -> "%Y-%m-%d %H:%i:%s";
            case "m" -> "%Y-%m-%d %H:%i:00";
            case "d" -> "%Y-%m-%d 00:00:00";
            case "M" -> "%Y-%m-01 00:00:00";
            case "y" -> "%Y-01-01 00:00:00";
            default -> "%Y-%m-%d %H:00:00";
        };
        
        // 构建查询条件
        LambdaQueryWrapper<LogEntity> wrapper = Wrappers.lambdaQuery();
        
        // 应用过滤条件
        if (StrUtil.isNotBlank(cluster)) {
            wrapper.eq(LogEntity::getCluster, cluster);
        }
        
        if (StrUtil.isNotBlank(namespaces)) {
            wrapper.in(LogEntity::getNamespace, QueryUtil.splitAndTrim(namespaces));
        } else if (StrUtil.isNotBlank(namespaceQuery)) {
            wrapper.like(LogEntity::getNamespace, namespaceQuery);
        }
        
        if (StrUtil.isNotBlank(pods)) {
            wrapper.in(LogEntity::getPod, QueryUtil.splitAndTrim(pods));
        } else if (StrUtil.isNotBlank(podQuery)) {
            wrapper.like(LogEntity::getPod, podQuery);
        }
        
        if (StrUtil.isNotBlank(containers)) {
            wrapper.in(LogEntity::getContainer, QueryUtil.splitAndTrim(containers));
        } else if (StrUtil.isNotBlank(containerQuery)) {
            wrapper.like(LogEntity::getContainer, containerQuery);
        }
        
        if (StrUtil.isNotBlank(logQuery)) {
            wrapper.like(LogEntity::getBody, logQuery);
        }
        
        if (startTime != null) {
            wrapper.ge(LogEntity::getTimestamp, startTime);
        }
        
        if (endTime != null) {
            wrapper.le(LogEntity::getTimestamp, endTime);
        }
        
        // 执行查询并收集结果
        List<Map<String, Object>> results = logMapper.selectTimeBucketStats(timeFormat, wrapper);
        List<Bucket> buckets = results.stream()
                .map(map -> Bucket.builder()
                        .time((String) map.get("time_bucket"))
                        .count((Long) map.get("count"))
                        .build())
                .collect(Collectors.toList());
        
        // 计算总数
        long total = buckets.stream().mapToLong(Bucket::getCount).sum();
        
        return Histogram.builder()
                .total(total)
                .histograms(buckets)
                .build();
    }
} 