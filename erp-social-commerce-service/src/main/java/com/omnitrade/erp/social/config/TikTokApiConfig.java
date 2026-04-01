package com.omnitrade.erp.social.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * TikTok Shop API 配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "tiktok.api")
public class TikTokApiConfig {
    
    /**
     * API基础URL
     */
    private String baseUrl = "https://open.tiktokapis.com/v2";
    
    /**
     * 沙箱环境API基础URL
     */
    private String sandboxBaseUrl = "https://sandbox-open.tiktokapis.com/v2";
    
    /**
     * 应用Key
     */
    private String appKey;
    
    /**
     * 应用密钥
     */
    private String appSecret;
    
    /**
     * 回调URL
     */
    private String redirectUri;
    
    /**
     * 请求超时时间（毫秒）
     */
    private int timeout = 30000;
    
    /**
     * 是否使用沙箱环境
     */
    private boolean sandbox = false;
    
    /**
     * 获取当前环境的Base URL
     */
    public String getEffectiveBaseUrl() {
        return sandbox ? sandboxBaseUrl : baseUrl;
    }
}