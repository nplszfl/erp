# ERP AI Assistant Service - 智能助手服务

## 🤖 简介

**v1.5.0** - AI驱动的智能助手系统

这是OmniTrade ERP系统的AI核心模块，提供智能客服、产品描述生成、多语言翻译等AI能力。

## 🔥 核心功能

### 1. AI智能客服
- ⏳ 知识库问答
- ⏳ 多语言上下文理解
- ⏳ 智能问题路由
- ⏳ 对话历史管理

### 2. 产品描述生成
- ⏳ AI多语言描述生成
- ⏳ SEO优化
- ⏳ 批量生成
- ⏳ 描述模板定制

### 3. AI翻译服务
- ⏳ 多语言实时翻译
- ⏳ 商品信息翻译
- ⏳ 客户消息翻译

### 4. AI分析服务
- ⏳ 评论情感分析
- ⏳ 客户意图识别
- ⏳ 智能推荐

## 🏗️ 架构设计

```
erp-ai-assistant-service/
/
├── controller/
│   ├── AIChatController.java         # AI聊天控制器
│   ├── ProductDescController.java    # 产品描述生成控制器
│   └── TranslationController.java    # 翻译服务控制器
├── service/
│   ├── AIChatService.java            # AI聊天服务
│   ├── ProductDescService.java        # 产品描述生成服务
│   └── TranslationService.java       # 翻译服务
└── model/
    ├── ChatMessage.java              # 聊天消息
    └── ProductDescription.java       # 产品描述
```

## 🚀 快速启动

```bash
cd erp-ai-assistant-service
mvn clean package -DskipTests
java -jar target/erp-ai-assistant-service-1.5.0.jar
```

## 📖 API文档

```
http://localhost:8088/swagger-ui.html
```

---

**状态：** 🚧 规划中
**优先级：** ⭐⭐⭐⭐⭐
