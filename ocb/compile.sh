# 注意在x86上需交叉编译静态二进制
go install go.opentelemetry.io/collector/cmd/builder@latest
builder --config=build-config.yml