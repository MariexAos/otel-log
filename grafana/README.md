# Grafana 日志可视化

本目录包含一个用于日志可视化的 Grafana Docker Compose 配置，主要用于展示存储在 Doris 数据库中的日志数据。日志收集流程为：通过 OpenTelemetry 收集日志，发送到 Kafka，然后加载到 Doris 中。

## 前置条件

- 已安装 Docker 和 Docker Compose
- Kafka 服务已运行（在 `../kafka` 目录）
- Doris 服务已运行（在 `../doris` 目录）
- OpenTelemetry 收集器已配置为发送日志到 Kafka（在 `../otel-collector` 目录）

## 部署步骤

1. 确保 Kafka, Doris 和 OpenTelemetry 收集器已经启动：
   ```
   cd ../kafka
   docker-compose up -d
   
   cd ../doris
   docker-compose up -d
   
   cd ../otel-collector
   ./collector-start.sh
   ```

2. 在 Doris 中创建所需的数据库和表：
   ```
   curl -X POST --location-trusted -u root: \
     -H "Content-Type: application/json" \
     -d '{"sql":"CREATE DATABASE IF NOT EXISTS log_db"}' \
     http://localhost:8030/api/log_db/execute
   
   curl -X POST --location-trusted -u root: \
     -H "Content-Type: application/json" \
     -d @../doris/otel_logs_table_v2.sql \
     http://localhost:8030/api/log_db/execute
   
   curl -X POST --location-trusted -u root: \
     -H "Content-Type: application/json" \
     -d @../doris/routine_load_otel_logs_v2.sql \
     http://localhost:8030/api/log_db/execute
   ```

3. 启动 Grafana：
   ```
   docker-compose up -d
   ```
   
   或者直接使用提供的脚本：
   ```
   ./start.sh
   ```

4. 访问 Grafana：http://localhost:3000
   - 用户名：`admin`
   - 密码：`admin`

## 提供的仪表盘

### 1. 默认日志仪表盘

默认仪表盘包含以下内容：

- 最近日志面板：显示最新的日志消息
- 日志数量图表：显示一段时间内的日志频率

### 2. 日志查询面板

专门用于日志搜索和筛选的高级查询面板，包含以下功能：

- **关键词搜索**：可以输入关键词搜索日志内容
- **日志级别过滤**：可以按 INFO、WARN、ERROR 等级别筛选日志
- **时间范围选择**：可以选择不同的时间范围
- **日志统计图表**：按日志级别展示日志数量分布
- **详细日志表格**：包含时间、消息、日志级别、追踪ID等信息

通过这个查询面板，可以快速定位和分析系统中的问题日志。

## 自定义配置

- 修改 `provisioning/datasources/doris.yaml` 以更改 Doris 连接设置
- 在 `provisioning/dashboards/` 中添加更多仪表盘，以可视化日志的不同方面
- 编辑 `docker-compose.yml` 以自定义 Grafana 配置或安装其他插件

## 架构说明

整个日志系统采用了以下架构：

1. 应用程序通过 OpenTelemetry 收集日志
2. 日志被发送到 Kafka 主题 `otel-logs`
3. Doris 通过 Routine Load 从 Kafka 加载日志数据
4. Grafana 连接 Doris 数据库并提供日志可视化界面

这种架构具有高扩展性，适合处理大规模的日志数据，同时提供灵活的查询和可视化能力。 