# 下载kafka 3.9.0 的执行文件
sh kafka-topics.sh --create --topic otel-logs --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1