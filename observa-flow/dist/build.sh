#!/usr/bin/env bash

set -euo pipefail

# 应用名称，默认取目录名
APP_NAME=${APP_NAME:-"observa-flow"}

REGISTRY=${REGISTRY:-"opentelemetry"}

# 当前git commit短ID作为默认版本号
GIT_COMMIT=$(git rev-parse --short=7 HEAD)
APP_VERSION=${APP_VERSION:-$GIT_COMMIT}

# 多平台支持
PLATFORMS="linux/arm64"

# 镜像完整名
IMAGE_NAME="$REGISTRY/$APP_NAME:$APP_VERSION"
LATEST_IMAGE_NAME="$REGISTRY/$APP_NAME:latest"

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
  --tag $LATEST_IMAGE_NAME \
  .

echo "----------------------------------------"
echo "Docker image pushed successfully: $IMAGE_NAME"
echo "----------------------------------------"
