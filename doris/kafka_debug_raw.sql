CREATE TABLE kafka_debug_raw (
    id BIGINT, -- 新增 id 列作为键列
    raw_json TEXT -- 现在可以使用 TEXT/STRING 类型
)
ENGINE=OLAP
DUPLICATE KEY(id)
DISTRIBUTED BY HASH(id) BUCKETS 1
PROPERTIES ("replication_num" = "1");