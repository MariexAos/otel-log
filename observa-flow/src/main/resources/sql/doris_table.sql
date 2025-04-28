CREATE TABLE IF NOT EXISTS `otel_logs_test` (
                                                `timestamp` DATETIME COMMENT '日志时间',
                                                `severity` TEXT COMMENT '日志级别',
                                                `body` TEXT COMMENT '内容',
                                                `attributes` JSON COMMENT '属性',
                                                `trace_id` TEXT COMMENT '追踪ID',
                                                `span_id` TEXT COMMENT '跨度ID',
                                                `cluster` TEXT COMMENT '集群名称',
                                                `namespace` TEXT COMMENT '命名空间',
                                                `pod` TEXT COMMENT 'pod名称',
                                                `container` TEXT COMMENT '容器名称',
                                                INDEX idx_body (`body`) USING INVERTED PROPERTIES("parser" = "unicode", "support_phrase" = "true"),
                                                INDEX idx_trace_id(`trace_id`) USING INVERTED,
                                                INDEX idx_severity(`severity`) USING INVERTED,
                                                INDEX idx_cluster(`cluster`) USING INVERTED,
                                                INDEX idx_namespace(`namespace`) USING INVERTED,
                                                INDEX idx_pod(`pod`) USING INVERTED,
                                                INDEX idx_container(`container`) USING INVERTED
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