package com.crossborder.erp.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 亚马逊SP-API配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "platform.amazon")
public class AmazonConfig {

    /**
     * API基础URL
     */
    private String apiBaseUrl = "https://sellingpartnerapi-na.amazon.com";

    /**
     * 应用端点（US/EU/FE/NA）
     */
    private String region;

    /**
     * SP-API角色ARN
     */
    private String roleArn;

    /**
     * SP-API Client ID
     */
    private String clientId;

    /**
     * SP-API Client Secret
     */
    private String clientSecret;

    /**
     * AWS访问密钥
     */
    private String awsAccessKeyId;

    /**
     * AWS访问密钥
     */
    private String awsSecretKey;

    /**
     * 店铺ID
     */
    private String merchantId;

    /**
     * 市场ID（US: ATVPDKIKX0DER, UK: A1F83G8C2ARO7P等）
     */
    private String marketplaceId;
}
