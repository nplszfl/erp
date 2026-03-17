package com.crossborder.pricing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智能定价服务 API")
                        .description("AI驱动的自动定价系统 - v1.5.0\n\n" +
                                "核心功能：\n" +
                                "1. 竞品数据抓取 - 实时抓取Amazon、eBay、Shopee等平台的竞品价格\n" +
                                "2. 智能定价算法 - 基于竞品分析、成本、利润率、季节性等多因素定价\n" +
                                "3. 动态定价策略 - 自动调整价格，适应市场变化\n" +
                                "4. 价格历史追踪 - 记录价格变更历史，支持趋势分析")
                        .version("1.5.0")
                        .contact(new Contact()
                                .name("火球鼠")
                                .email("huohuoshu@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
