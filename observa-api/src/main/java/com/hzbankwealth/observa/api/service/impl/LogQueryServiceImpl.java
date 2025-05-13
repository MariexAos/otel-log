package com.hzbankwealth.observa.api.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzbankwealth.observa.api.mapper.LogMapper;
import com.hzbankwealth.observa.api.model.LogEntity;
import com.hzbankwealth.observa.api.model.logging.Logs;
import com.hzbankwealth.observa.api.service.LogQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogQueryServiceImpl implements LogQueryService {

    private final LogMapper logMapper;

    @Override
    public Logs queryLogs(
            String namespaces, String namespaceQuery,
            String pods, String podQuery,
            String containers, String containerQuery,
            String logQuery,
            LocalDateTime startTime, LocalDateTime endTime,
            String sort, Integer from, Integer size,
            String cluster) {

        LambdaQueryWrapper<LogEntity> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(logQuery)) {
            wrapper.apply("body MATCH_ALL {0}", logQuery);
        }

        if (StrUtil.isNotBlank(cluster)) {
            wrapper.eq(LogEntity::getCluster, cluster);
        }

        if (StrUtil.isNotBlank(namespaces)) {
            wrapper.in(LogEntity::getNamespace, namespaces);
        }

        if (StrUtil.isNotBlank(namespaceQuery)) {
            wrapper.like(LogEntity::getNamespace, namespaceQuery);
        }

        if (StrUtil.isNotBlank(pods)) {
            wrapper.in(LogEntity::getPod, pods);
        }

        if (StrUtil.isNotBlank(podQuery)) {
            wrapper.like(LogEntity::getPod, podQuery);
        }

        if (StrUtil.isNotBlank(containers)) {
            wrapper.in(LogEntity::getContainer, containers);
        }

        if (StrUtil.isNotBlank(containerQuery)) {
            wrapper.like(LogEntity::getContainer, containerQuery);
        }

        if (Objects.nonNull(startTime)) {
            wrapper.ge(LogEntity::getTimestamp, startTime);
        }

        if (Objects.nonNull(endTime)) {
            wrapper.le(LogEntity::getTimestamp, endTime);
        }

        if (StrUtil.isBlank(sort) || sort.equals("desc")) {
            wrapper.orderByDesc(LogEntity::getTimestamp);
        } else {
            wrapper.orderByAsc(LogEntity::getTimestamp);
        }

        int pageNum = from / size + 1;

        Page<LogEntity> page = new Page<>(pageNum, size);
        Page<LogEntity> result = logMapper.selectPage(page, wrapper);
        return Logs.builder()
                .total(page.getTotal())
                .records(result.getRecords())
                .build();
    }
} 