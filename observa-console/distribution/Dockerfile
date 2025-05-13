# Dockerfile 在 distribution 目录中
FROM node:22.14.0-alpine AS builder

RUN corepack enable && corepack prepare pnpm@latest --activate

WORKDIR /app

# 注意路径是 ../ 将上层项目代码拷贝进来
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile

COPY . ./
RUN pnpm run build

FROM nginx:stable-alpine

# 替换 nginx 静态目录
RUN rm -rf /usr/share/nginx/html/*
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 nginx 配置文件（当前目录下）
COPY ./distribution/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
