# 基础镜像
FROM node:18-alpine AS builder

# 设置工作目录
WORKDIR /app

# 复制package.json和依赖
COPY erp-web/package*.json ./erp-web/

# 安装依赖
RUN cd erp-web && npm install

# 复制源代码
COPY erp-web ./erp-web/

# 构建前端
RUN cd erp-web && npm run build

# Nginx镜像
FROM nginx:alpine

# 复制构建产物到Nginx
COPY --from=builder /app/erp-web/dist /usr/share/nginx/html

# 复制Nginx配置
COPY docker/nginx.conf /etc/nginx/nginx.conf

# 暴露端口
EXPOSE 80

# 启动Nginx
CMD ["nginx", "-g", "daemon off;"]
