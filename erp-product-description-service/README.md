# ERP Product Description Service 产品描述生成服务

## 🤖 简介

**v1.5.0** - AI驱动的多语言产品描述生成系统

这是OmniTrade ERP系统的核心AI模块，通过大语言模型（LLM）自动生成吸引人、专业、符合SEO规范的产品描述。

## 🔥 核心功能

### 1. AI多语言描述生成
- ✅ 支持中英文、日文等多种语言
- ✅ 自动检测源语言
- ✅ 风格符合平台规范
- ✅ 突出产品核心卖点

### 2. SEO优化
- ✅ 关键词优化
- ✅ 标题优化（限制长度）
- ✅ 描述优化（可读性）
- ✅ 关键词密度分析
- ✅ SEO评分和建议

### 3. 产品亮点提炼
- ✅ 自动识别产品优势
- ✅ 重要性排序
- ✅ 结构化展示
- ✅ 对应产品特性

### 4. A/B测试支持
- ✅ 生成多个描述版本
- ✅ 自动对比指标
- ✅ 选择最佳版本
- ✅ 支持手动选择

### 5. 批量生成
- ✅ 支持批量处理多个产品
- ✅ 异步任务队列
- ✅ 进度追踪
- ✅ 失败重试

### 6. 描述模板定制
- ✅ 多平台模板（Amazon、eBay、Shopee等）
- ✅ 模板变量替换
- ✅ 自定义模板
- ✅ 模板版本管理

## 🏗️ 架构设计

```
erp-product-description-service/
├── controller/
│   └── ProductDescriptionController.java
├── service/
│   ├── ProductDescriptionService.java
│   └── impl/
│       └── ProductDescriptionServiceImpl.java
├── dto/
│   ├── DescriptionGenerationRequest.java
│   └── DescriptionGenerationResponse.java
├── config/
│   └── (LLM配置、SEO配置）
└── resources/
    └── application.yml
```

## 🚀 快速启动

### 前置要求
- JDK 21+
- Maven 3.9+
- MySQL 8.0+
- Redis
- LLM API密钥（OpenAI或DeepSeek）

### 启动步骤

1. **初始化数据库**
```bash
mysql -u root -p < database/product-description-service.sql
```

2. **配置API密钥**
编辑 `src/main/resources/application.yml`，配置LLM API密钥：
```yaml
llm:
  deepseek:
    enabled: true
    api-key: YOUR_DEEPSEEK_API_KEY
```

3. **启动服务**
```bash
cd erp-product-description-service
mvn clean package -DskipTests
java -jar target/erp-product-description-service-1.5.0.jar
```

## 🌐 API文档

```
http://localhost:8091/swagger-ui.html
```

## 📖 核心API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/product-description/health` | GET | 健康检查 |
| `/api/v1/product-description/generate` | POST | 生成产品描述 |
| `/api/v1/product-description/batch-generate` | POST | 批量生成 |
| `/api/v1/product-description/multi-language` | POST | 多语言生成 |
| `/api/v1/product-description/seo-optimize` | POST | SEO优化 |
| `/api/v1/product-description/highlights/{id}` | GET | | 提炼亮点 |
| `/api/v1/product-description/ab-test` | POST | A/B测试 |
| `/api/v1/product-description/save` | POST | 保存描述 |
| `/api/v1/product-description/list/{id}` | GET | 获取描述 |
| `/api/v1/product-description/batch-import` | POST | 批量导入 |

## 💡 使用示例

### 生成产品描述

```bash
curl -X POST http://localhost:8091/api/v1/product-description/generate \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "productName": "无线蓝牙耳机",
    "keywords": ["高保真", "降噪", "蓝牙"],
    "features": [
      "高保真音质",
      "主动降噪",
      "30小时续航",
      "舒适佩戴"
    ],
    "targetLanguage": "zh",
    "targetPlatform": "amazon",
    "enableSEO": true,
    "extractHighlights": true,
    "generateShortTitle": true
  }'
```

### 响应示例

```json
{
  "descriptionId": "550e8400-e29b-41d4-a716-4466554402",
  "productId": 1,
  "productName": "无线蓝牙耳机",
  "content": "【无线蓝牙耳机】\n\n产品特点：\n1. 高保真音质\n2. 主动降噪\n3. 30小时续航\n4. 舒适佩戴\n\n这是一款产品，具有优秀的性能和可靠的质量...",
  "shortTitle": "无线蓝牙耳机...",
  "language": "zh",
  "platform": "amazon",
  "versionNumber": 1,
  "seoResult": {
    "title": "无线蓝牙耳机",
    "description": "优化后的描述...",
    "primaryKeywords": ["高保真", "降噪"],
    "readabilityScore": 75.5,
    "seoScore": 82.3,
    "suggestions": []
  },
  "highlights": [
    {
      "id": "highlight-1",
      "title": "核心优势 1",
      "description": "高保真音质",
      "importance": 5,
      "feature": "高保真音质"
    }
  ],
  "model": "deepseek-chat",
  "generationTime": 1250,
  "createTime": "2026-03-17T22:50:00"
}
```

### 多语言生成

```bash
curl -X POST http://localhost:8091/api/v1/product-description/multi-language \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "languages": ["zh", "en", "ja", "de"]
  }'
```

### A/B测试

```bash
curl -X POST http://localhost:8091/api/v1/product-description/ab-test \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "versionCount": 3
  }'
```

## 🧪 SEO优化策略

### 标题优化
- 长度控制：60字符以内
- 关键词前置：重要关键词在前
- 去除冗余：删除无意义词
- 吸引力：使用标点符号

### 描述优化
- 长度控制：100-200字符
- 句子结构：3-5个短句
- 关键词密度：2-3%
- 可读性评分：目标70+

### 关键词策略
- 主关键词：3-5个
- 长尾关键词：5-8个
- 密度控制：1-5%
- 自然分布：关键词均匀分布

## 📊 性能指标

| 指标 | 目标值 |
|------|--------|
| 描述生成时间 | < 3s |
| SEO优化时间 | < 1s |
| 批量生成（100个） | < 30s |
| 多语言生成（4语言） | < 10s |

---

**版本：** v1.5.0
**作者：** 火球鼠 🔥
**状态：** 🚀 85%完成
