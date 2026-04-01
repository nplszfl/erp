package com.crossborder.tenant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OmniTrade ERP - Tenant Service API")
                        .description("多租户管理服务 API")
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("OmniTrade")
                                .email("support@omnitrade.com")));
    }
}