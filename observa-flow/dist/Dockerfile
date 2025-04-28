FROM openjdk:17-jdk-slim
# 环境变量（可根据需要添加）
ENV TZ=Asia/Shanghai \
    LANG=C.UTF-8 \
    JAVA_OPTS=""

WORKDIR /app

COPY target/observa-flow.jar observa-flow.jar

# 健康检查（可选但推荐）
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar observa-flow.jar"]