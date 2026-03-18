#!/bin/bash
# 停止 OmniTrade ERP 最小化部署

set -e

echo "🛑 停止 OmniTrade ERP 最小化部署..."

cd /Users/huanghuixiang/.openclaw/workspace/OmniTradeERP

# 停止容器
docker-compose -f docker-compose.minimal.yml down

echo "✅ 已停止！"
