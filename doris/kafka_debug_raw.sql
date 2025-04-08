CREATE TABLE kafka_debug_raw (
    id BIGINT NOT NULL AUTO_INCREMENT,
    raw_json TEXT, -- 现在可以使用 TEXT/STRING 类型
    INDEX idx_message (`raw_json`) USING INVERTED PROPERTIES("parser" = "unicode", "support_phrase" = "true")
)
ENGINE=OLAP
DUPLICATE KEY(id)
DISTRIBUTED BY HASH(id) BUCKETS 1
PROPERTIES ("replication_num" = "1");