package com.omnitrade.erp.social.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("社交电商API")
                        .description("跨境电商ERP系统 - 社交电商整合服务API文档")
                        .version("1.6.0")
                        .contact(new Contact()
                                .name("OmniTrade ERP")
                                .email("support@omnitrade.com")));
    }
}