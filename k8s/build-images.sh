#!/bin/bash

# 构建并推送Docker镜像

echo "🔨 开始构建Docker镜像..."

# Gateway
echo "构建Gateway镜像..."
docker build -f docker/Dockerfile.gateway -t nplszfl/erp-gateway:latest .
docker push nplszfl/erp-gateway:latest

# Order Service
echo "构建Order Service镜像..."
docker build -f docker/Dockerfile.order -t nplszfl/erp-order-service:latest .
docker push nplszfl/erp-order-service:latest

# Platform Service
echo "构建Platform Service镜像..."
# TODO: 添加Platform Service Dockerfile

# Product Service
echo "构建Product Service镜像..."
# TODO: 添加Product Service Dockerfile

# User Service
echo "构建User Service镜像..."
# TODO: 添加User Service Dockerfile

echo "✅ 镜像构建完成！"
