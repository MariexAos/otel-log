#!/usr/bin/env bash

set -euo pipefail

# 应用名称，默认取目录名
APP_NAME=${APP_NAME:-"observa-flow"}

# 目标环境（dev本地测试，test测试仓库，prod生产仓库）
TARGET_ENV=${TARGET_ENV:-dev}

# 根据不同环境设置不同的Registry地址
if [ "$TARGET_ENV" = "dev" ]; then
  REGISTRY=${REGISTRY:-"opentelemetry"}
elif [ "$TARGET_ENV" = "test" ]; then
  REGISTRY=${REGISTRY:-"harbor.test.xxx.com/dev"}
elif [ "$TARGET_ENV" = "prod" ]; then
  REGISTRY=${REGISTRY:-"harbor.prod.xxx.com/prod"}
else
  echo "Unknown TARGET_ENV: $TARGET_ENV"
  exit 1
fi

# 当前git commit短ID作为默认版本号
GIT_COMMIT=$(git rev-parse --short=7 HEAD)
APP_VERSION=${APP_VERSION:-$GIT_COMMIT}

# 多平台支持
PLATFORMS="linux/arm64"

# 镜像完整名
IMAGE_NAME="$REGISTRY/$APP_NAME:$APP_VERSION"

echo "----------------------------------------"
echo "Building Docker image: $IMAGE_NAME for environment: $TARGET_ENV"
echo "----------------------------------------"

# 确保buildx builder存在
docker buildx create --use --name multiarch-builder || echo "buildx builder already exists"
docker buildx inspect multiarch-builder --bootstrap

# 开始build并push
docker buildx build \
  --file dist/Dockerfile \
  --platform $PLATFORMS \
  --load \
  --tag $IMAGE_NAME \
  .

echo "----------------------------------------"
echo "Docker image pushed successfully: $IMAGE_NAME"
echo "----------------------------------------"
