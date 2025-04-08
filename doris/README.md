# 常见问题
## 1.docker rm -rf 后无法启动FE
docker rm -rf container后重启会容器IP会重新分配，但原始节点信息被记录在元数据内，被视作新的节点，导致选举时出现新的节点和历史节点，新节点会fallback到UNKNOWN状态；

采用host或者bridge模式时启动时注意指定PRIORITY_NETWORK=${HOST_IP/bridge指定IP}；

[元数据运维](https://doris.apache.org/zh-CN/docs/admin-manual/trouble-shooting/metadata-operation)