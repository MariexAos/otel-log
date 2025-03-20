CREATE ROUTINE LOAD log_db.load_otel_logs_v2 ON otel_logs_v2
COLUMNS(
    raw_json = raw_string,
    -- 直接使用json_string函数提取全路径的值
    log_time = FROM_UNIXTIME(CAST(json_string('$.timeUnixNano', raw_json) AS BIGINT)/1000000000),
    log_time_nano = CAST(json_string('$.timeUnixNano', raw_json) AS BIGINT),
    observed_time = FROM_UNIXTIME(CAST(json_string('$.observedTimeUnixNano', raw_json) AS BIGINT)/1000000000),
    message = json_string('$.body.stringValue', raw_json),
    attributes = json_string('$.attributes', raw_json),
    trace_id = json_string('$.traceId', raw_json),
    span_id = json_string('$.spanId', raw_json)
)
PROPERTIES (
  "desired_concurrent_number" = "1",
  "max_batch_interval" = "60",
  "max_batch_rows" = "200000",
  "max_batch_size" = "209715200",
  "strict_mode" = "false",
  "format" = "json",
  "json_root" = "$.resourceLogs[*].scopeLogs[*].logRecords",
  "strip_outer_array" = "true"
)
FROM KAFKA (
  "kafka_broker_list" = "127.0.0.1:9092",
  "kafka_topic" = "otel-logs",
  "property.group.id" = "doris_consumer_group",
  "property.kafka_default_offsets" = "OFFSET_END"
);