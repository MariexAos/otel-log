FROM golang:1.23 AS builder
WORKDIR /src
COPY ./build-config.yaml /src/build-config.yaml
RUN go install go.opentelemetry.io/collector/cmd/builder@latest
    RUN CGO_ENABLED=0 \
        GOOS=linux \
        GOARCH=arm64 \
        builder --config=/src/build-config.yaml

FROM gcr.io/distroless/static-debian11
# 环境变量（可根据需要添加）
ENV TZ=Asia/Shanghai \
    LANG=C.UTF-8

WORKDIR /app

COPY --from=builder /src/tmp/otelcol otelcol

ENTRYPOINT ["./otelcol"]
