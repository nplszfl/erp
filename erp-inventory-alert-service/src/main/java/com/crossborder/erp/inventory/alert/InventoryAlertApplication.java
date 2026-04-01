package com.crossborder.erp.inventory.alert;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 库存预警服务启动类
 */
@SpringBootApplication
@MapperScan("com.crossborder.erp.inventory.alert.mapper")
@EnableScheduling
public class InventoryAlertApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryAlertApplication.class, args);
    }
}