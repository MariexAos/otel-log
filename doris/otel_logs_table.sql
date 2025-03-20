CREATE TABLE `otel_logs` (
  `timestamp` datetime,
  `log_level` varchar(10),
  `message` text,
  `service_name` varchar(255),
  `instance_id` varchar(255),
  `region` varchar(50),
  `file_path` varchar(512),
  `pod` varchar(255),
  `namespace` varchar(255),
  `container` varchar(255),
  `labels` json,
  `extra` json,
  INDEX idx_message (`message`) USING INVERTED PROPERTIES("parser" = "unicode", "support_phrase" = "true"),
  INDEX idx_service (`service_name`) USING INVERTED
)
ENGINE=OLAP
DUPLICATE KEY(`timestamp`)
PARTITION BY RANGE(`timestamp`) ()
DISTRIBUTED BY HASH(`service_name`) BUCKETS 10
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