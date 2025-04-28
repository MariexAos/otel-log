# OTEL Kafka Streams to Doris

基于Spring Boot和Kafka Streams的应用，用于将OpenTelemetry JSON数据写入Apache Doris数据库。

## 功能特性

- 使用Kafka Streams (3.9.0) 处理OTEL JSON数据
- 实现Fanout模式将数据写入Doris DUPLICATE KEY模型表
- 批量写入优化，提高性能
- 支持Exactly Once语义保证
- 写入失败自动重试机制
- 可监控的REST API接口

## 系统架构

```
+----------------+      +---------------------+      +-------------------+
| OTEL Collector |----->| Kafka (Topic/Queue) |----->| Kafka Streams App |
+----------------+      +---------------------+      +-------------------+
                                                             |
                                                             | Batch Write
                                                             v
                                                     +----------------+
                                                     | Apache Doris   |
                                                     | (DUPLICATE KEY)|
                                                     +----------------+
```

## 技术栈

- Spring Boot 3.x
- Apache Kafka 3.9.0
- Kafka Streams API
- Apache Doris (MySQL协议)
- HikariCP连接池
- Spring Retry

## 运行要求

- JDK 17+
- Apache Kafka 3.9.0+
- Apache Doris 2.x+

## 配置说明

主要配置项在`application.yml`文件中:

- Kafka连接参数
- Kafka Streams配置
- Doris数据库连接参数
- 批处理大小和时间间隔
- 重试参数配置

## 启动方法

1. 创建Doris表结构

```bash
mysql -h <doris_host> -P 9030 -u root -p < src/main/resources/sql/doris_table.sql
```

2. 修改`application.yml`中的配置，尤其是Kafka和Doris连接参数

3. 构建应用

```bash
./mvnw clean package
```

4. 运行应用

```bash
java -jar target/otel-kafka-streams-1.0.0.jar
```

## API接口

- `/api/stats` - 获取应用运行统计信息
- `/api/stats/health` - 健康检查接口

## 性能优化

本应用实现了以下性能优化:

1. 批量写入Doris，减少网络往返
2. 使用HikariCP连接池优化数据库连接
3. 使用ConcurrentLinkedQueue实现高效并发处理
4. 定时刷新策略，平衡延迟和吞吐量
5. 写入失败指数退避重试策略

## Doris表设计

使用DUPLICATE KEY模型设计表结构，支持高效的数据写入和查询：

- 使用id作为主键，支持去重和更新
- 使用动态分区优化数据管理
- 配置适当的分桶数提高并行性
- 默认3副本确保数据可靠性 