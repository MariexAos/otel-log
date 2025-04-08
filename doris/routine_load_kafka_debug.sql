CREATE ROUTINE LOAD kafka_debug_job ON kafka_debug_raw
COLUMNS(raw_json)
PROPERTIES
(
    "format" = "json",
    "jsonpaths" = "[\"$\"]",
    "strip_outer_array" = "false"
)
FROM KAFKA
(
  "kafka_broker_list" = "host.orb.internal:9092",
  "kafka_topic" = "otel-logs",
  "property.group.id" = "doris_consumer_group",
  "property.kafka_default_offsets" = "OFFSET_END"
);