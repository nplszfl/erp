# OmniTrade ERP 环境搭建指南

## 📋 系统要求

- **操作系统：** macOS / Linux / Windows (WSL2)
- **JDK：** Java 21（已安装 ✓）
- **Docker：** 已安装 ✓

---

## 1️⃣ Maven 安装

### 下载（使用国内镜像）

```bash
# 进入下载目录
cd ~/Downloads

# 下载 Maven 3.9.9
curl -O https://mirrors.aliyun.com/apache/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz
```

### 解压和安装

```bash
# 解压
tar -xzf apache-maven-3.9.9-bin.tar.gz

# 移动到系统目录
sudo mv apache-maven-3.9.9 /usr/local/maven

# 清理下载文件
rm apache-maven-3.9.9-bin.tar.gz
```

### 配置环境变量

```bash
# 编辑 shell 配置文件（根据你使用的 shell 选择）
# 如果是 zsh（macOS默认）：
echo 'export M2_HOME=/usr/local/maven' >> ~/.zshrc
echo 'export PATH=$M2_HOME/bin:$PATH' >> ~/.zshrc

# 重新加载配置
source ~/.zshrc
```

### 验证安装

```bash
mvn -version
```

**预期输出：**
```
Apache Maven 3.9.9 (...)
Maven home: /usr/local/maven
Java version: 21.0.10, vendor: Oracle Corporation
```

---

## 2️⃣ Nacos 安装（微服务注册中心）

### 下载

```bash
cd ~/Downloads
curl -O https://github.com/alibaba/nacos/releases/download/2.4.1/nacos-server-2.4.1.tar.gz
```

### 解压和安装

```bash
# 解压
tar -xzf nacos-server-2.4.1.tar.gz

# 移动到系统目录
sudo mv nacos /usr/local/nacos

# 清理
rm nacos-server-2.4.1.tar.gz
```

### 启动 Nacos（单机模式）

```bash
cd /usr/local/nacos
./bin/startup.sh -m standalone
```

### 访问 Nacos 控制台

- **地址：** http://localhost:8848/nacos
- **用户名：** nacos
- **密码：** nacos

### 停止 Nacos

```bash
/usr/local/nacos/bin/shutdown.sh
```

---

## 3️⃣ MySQL 安装（可选，本地开发用）

### 使用 Homebrew 安装（推荐）

```bash
brew install mysql
brew services start mysql
```

### 初始化数据库

```sql
-- 连接 MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE erp_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 退出
exit;
```

---

## 4️⃣ Redis 安装（可选，缓存用）

### 使用 Homebrew 安装

```bash
brew install redis
brew services start redis
```

---

## 🚀 启动 ERP 系统

### 方式一：启动智能定价服务（v1.5.0）

```bash
cd ~/OmniTradeERP  # 或你的项目路径
./quick-start.sh
```

### 方式二：启动所有微服务

```bash
cd ~/OmniTradeERP
./start.sh
```

---

## 🌐 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 智能定价服务 | http://localhost:8087 | v1.5.0 新服务 |
| API文档 | http://localhost:8087/swagger-ui.html | Swagger UI |
| 健康检查 | http://localhost:8087/api/v1/pricing/health | 服务状态 |
| Nacos | http://localhost:8848/nacos | 注册中心 |
| Gateway | http://localhost:8080 | API网关 |

---

## 🐛 常见问题

### Q: Maven 下载慢怎么办？
**A:** 使用国内镜像（本指南已提供阿里云链接）

### Q: Nacos 启动失败？
**A:** 检查端口 8848 是否被占用
```bash
lsof -i:8848
```

### Q: 服务启动失败？
**A:** 检查 Nacos 是否已启动，数据库连接是否正确

### Q: 想用 Docker 启动所有服务？
**A:** 使用项目提供的 docker-compose.yml
```bash
docker-compose up -d
```

---

## 📞 需要帮助？

联系 **火球鼠** 🔥

---

**最后更新：** 2026-03-17
**版本：** v1.5.0
