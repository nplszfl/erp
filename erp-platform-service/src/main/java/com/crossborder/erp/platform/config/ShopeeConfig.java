package com.crossborder.erp.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Shopee API配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "platform.shopee")
public class ShopeeConfig {

    /**
     * API基础URL
     */
    private String apiBaseUrl = "https://partner.shopee.com";

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * Shop ID（店铺ID）
     */
    private String shopId;

    /**
     * Partner ID（合作伙伴ID）
     */
    private Long partnerId;
}
