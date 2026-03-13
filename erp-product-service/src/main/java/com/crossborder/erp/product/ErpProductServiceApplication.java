package com.crossborder.erp.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ErpProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpProductServiceApplication.class, args);
    }
}
