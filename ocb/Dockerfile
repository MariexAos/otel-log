FROM gcr.io/distroless/static-debian11
# 环境变量（可根据需要添加）
ENV TZ=Asia/Shanghai \
    LANG=C.UTF-8

WORKDIR /app

COPY tmp/otelcol otelcol

ENTRYPOINT ["./otelcol"]
