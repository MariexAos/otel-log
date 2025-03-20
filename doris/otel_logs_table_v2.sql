CREATE TABLE otel_logs_v2 (
    `log_time` DATETIME NOT NULL COMMENT "日志时间，从timeUnixNano转换",
    `log_time_nano` BIGINT NOT NULL COMMENT "原始纳秒时间戳",
    `observed_time` DATETIME COMMENT "观测时间",
    `message` TEXT COMMENT "日志内容",
    `attributes` ARRAY<JSON> COMMENT "日志属性数组，保持原始结构",
    `trace_id` VARCHAR(64) COMMENT "追踪ID",
    `span_id` VARCHAR(64) COMMENT "Span ID",
    `source_json` TEXT COMMENT "原始JSON数据"
)
ENGINE=OLAP
DUPLICATE KEY(`log_time`)
PARTITION BY RANGE(`log_time`) ()
DISTRIBUTED BY HASH(`log_time_nano`) BUCKETS 10
PROPERTIES (
"compression" = "ZSTD",
"replication_num" = "1",
"compaction_policy" = "time_series",
"dynamic_partition.enable" = "true",
"dynamic_partition.create_history_partition" = "true",
"dynamic_partition.time_unit" = "DAY",
"dynamic_partition.start" = "-30",
"dynamic_partition.end" = "1",
"dynamic_partition.prefix" = "p",
"dynamic_partition.buckets" = "60",
"dynamic_partition.replication_num" = "1"
);