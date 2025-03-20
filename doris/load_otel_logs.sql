CREATE ROUTINE LOAD log_db.load_otel_logs ON otel_logs
COLUMNS(timestamp, log_level, message, file_path)
PROPERTIES (
  "desired_concurrent_number" = "1",
  "max_batch_interval" = "60",
  "max_batch_rows" = "200000",
  "max_batch_size" = "209715200",
  "strict_mode" = "false",
  "format" = "json",
  "json_root" = "$.resourceLogs",
  "jsonpaths" = "[
    \"$.scopeLogs[*].logRecords[*].attributes[?(@.key=='timestamp')].value.stringValue\",
    \"$.scopeLogs[*].logRecords[*].attributes[?(@.key=='log_level')].value.stringValue\",
    \"$.scopeLogs[*].logRecords[*].body.stringValue\",
    \"$.scopeLogs[*].logRecords[*].attributes[?(@.key=='log.file.name')].value.stringValue\"
  ]",
  "strip_outer_array" = "true"
)
FROM KAFKA (
  "kafka_broker_list" = "127.0.0.1:9092",
  "kafka_topic" = "otel-logs",
  "property.group.id" = "doris_consumer_group",
  "property.kafka_default_offsets" = "OFFSET_END"
);