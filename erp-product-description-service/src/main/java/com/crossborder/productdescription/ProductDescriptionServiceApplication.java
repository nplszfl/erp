package com.crossborder.productdescription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 产品描述生成服务 - v1.5.0 核心AI模块
 *
 * 主要功能：
 * 1. AI多语言描述生成 - 自动生成中英文等语言的产品描述
 * 2. SEO优化 - 自动优化关键词、标题、描述
 * 3. 批量生成 - 支持批量处理多个产品
 * 4. 描述模板定制 - 支持不同平台和产品的模板
 * 5. 亮点提炼 - 自动提炼产品卖点
 * 6. A/B测试支持 - 生成多个版本供选择
 *
 * 技术实现：
 * - LLM API集成（OpenAI/DeepSeek）
 * - SEO关键词分析
 * - 多语言翻译
 * - 模板引擎
 *
 * @author 火球鼠
 * @since 2026-03-17
 */
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class ProductDescriptionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductDescriptionServiceApplication.class, args);
    }
}
