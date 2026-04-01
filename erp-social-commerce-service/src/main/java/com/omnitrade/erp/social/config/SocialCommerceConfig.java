package com.omnitrade.erp.social.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 社交电商服务配置
 */
@Configuration
public class SocialCommerceConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);  // 30秒连接超时
        factory.setReadTimeout(30000);     // 30秒读取超时
        return new RestTemplate(factory);
    }
}