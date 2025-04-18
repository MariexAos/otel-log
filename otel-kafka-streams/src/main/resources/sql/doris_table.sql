CREATE TABLE IF NOT EXISTS `otel_logs_test` (
                                                `timestamp` DATETIME COMMENT '日志时间',
                                                `severity` TEXT COMMENT '日志级别',
                                                `body` TEXT COMMENT '内容',
                                                `attributes` JSON COMMENT '属性',
                                                `trace_id` TEXT COMMENT '追踪ID',
                                                `span_id` TEXT COMMENT '跨度ID'
)
    ENGINE=OLAP
    DUPLICATE KEY(`timestamp`)
    PARTITION BY RANGE(`timestamp`) ()
    DISTRIBUTED BY RANDOM BUCKETS 10
    PROPERTIES (
                   "compression" = "zstd",
                   "compaction_policy" = "time_series",
                   "dynamic_partition.enable" = "true",
                   "dynamic_partition.create_history_partition" = "true",
                   "dynamic_partition.time_unit" = "DAY",
                   "dynamic_partition.start" = "-30",
                   "dynamic_partition.end" = "1",
                   "dynamic_partition.prefix" = "p",
                   "dynamic_partition.buckets" = "10",
                   "dynamic_partition.replication_num" = "1",
                   "replication_num" = "1"
               );