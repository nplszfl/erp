#!/bin/bash

# 部署脚本

echo "🚀 开始部署跨境电商ERP系统到K8s..."

# 创建命名空间
echo "📦 创建命名空间..."
kubectl create namespace erp --dry-run=client -o yaml | kubectl apply -f -

# 部署MySQL
echo "🗄️  部署MySQL..."
kubectl apply -f k8s/mysql.yaml

# 等待MySQL就绪
echo "⏳ 等待MySQL就绪..."
kubectl wait --for=condition=ready pod -l app=mysql -n erp --timeout=300s

# 部署Redis
echo "💾 部署Redis..."
kubectl apply -f k8s/redis.yaml

# 等待Redis就绪
echo "⏳ 等待Redis就绪..."
kubectl wait --for=condition=ready pod -l app=redis -n erp --timeout=300s

# 部署Nacos
echo "🔧 部署Nacos..."
kubectl apply -f k8s/nacos.yaml

# 等待Nacos就绪
echo "⏳ 等待Nacos就绪..."
kubectl wait --for=condition=ready pod -l app=nacos -n erp --timeout=300s

# 部署Gateway
echo "🌐 部署Gateway..."
kubectl apply -f k8s/gateway.yaml

# 部署其他服务
echo "⚙️  部署微服务..."
kubectl apply -f k8s/services.yaml

echo ""
echo "✅ 部署完成！"
echo ""
echo "📋 查看Pod状态："
echo "kubectl get pods -n erp"
echo ""
echo "📋 查看Service："
echo "kubectl get svc -n erp"
echo ""
echo "📋 查看日志："
echo "kubectl logs -f deployment/erp-gateway -n erp"
echo ""
echo "🌐 访问Gateway："
echo "kubectl get svc erp-gateway-service -n erp"
