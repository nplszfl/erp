package com.crossborder.aiassistant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * LLM API配置
 * 
 * 支持多种LLM provider: DeepSeek, OpenAI, Azure OpenAI等
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LLMConfig {

    /** 是否启用真实API调用（默认false，使用模拟回复） */
    private boolean enabled = false;

    /** API Key */
    private String apiKey = "";

    /** API Base URL */
    private String baseUrl = "https://api.deepseek.com";

    /** 模型名称 */
    private String model = "deepseek-chat";

    /** 请求超时时间（毫秒） */
    private int timeout = 30000;

    /** 最大Token数 */
    private int maxTokens = 2000;

    /** Temperature参数 */
    private double temperature = 0.7;

    /** Provider类型: deepseek, openai, azure */
    private String provider = "deepseek";

    /**
     * 检查是否配置了有效的API Key
     */
    public boolean isConfigured() {
        return enabled && apiKey != null && !apiKey.isEmpty();
    }
}