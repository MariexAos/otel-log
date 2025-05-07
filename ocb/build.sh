# 多平台支持
PLATFORMS="linux/arm64"
# 应用名称，默认取目录名
APP_NAME=${APP_NAME:-"otelcol"}
# 镜像完整名
IMAGE_NAME="$REGISTRY/$APP_NAME:$APP_VERSION"
REGISTRY=${REGISTRY:-"opentelemetry"}
# 当前git commit短ID作为默认版本号
GIT_COMMIT=$(git rev-parse --short=7 HEAD)
APP_VERSION=${APP_VERSION:-$GIT_COMMIT}
# 镜像完整名
IMAGE_NAME="$REGISTRY/$APP_NAME:$APP_VERSION"
LATEST_IMAGE_NAME="$REGISTRY/$APP_NAME:latest"

docker buildx build \
  --file Dockerfile \
  --platform $PLATFORMS \
  --load \
  --tag $IMAGE_NAME \
  --tag $LATEST_IMAGE_NAME \
  .