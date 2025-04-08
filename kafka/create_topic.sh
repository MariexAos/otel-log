# 下载kafka 3.9.0 的执行文件
sh kafka-topics.sh --create --topic otel-logs --bootstrap-server host.orb.internal:9092 --partitions 1 --replication-factor 1
sh kafka-topics.sh --create --topic otel-proto --bootstrap-server host.orb.internal:9092 --partitions 1 --replication-factor 1

kcat -b host.orb.internal:9092 -t otel-logs -C -o end -u
kcat -b host.orb.internal:9092 -t otel-proto -C -o end -u