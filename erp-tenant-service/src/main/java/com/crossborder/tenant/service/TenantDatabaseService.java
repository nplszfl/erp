package com.crossborder.tenant.service;

import com.crossborder.tenant.entity.Tenant;
import com.crossborder.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租户数据库服务 - 为每个租户创建独立的Schema
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantDatabaseService {

    private final TenantRepository tenantRepository;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 创建租户数据库（Schema）
     * 每个租户拥有独立的Schema，实现数据完全隔离
     */
    @Transactional
    public void createTenantSchema(String tenantId) {
        String schemaName = "tenant_" + tenantId.replace("-", "_");
        
        try {
            // 创建Schema
            jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
            
            // 创建订单表
            String ordersSql = "CREATE TABLE IF NOT EXISTS " + schemaName + ".orders (" +
                "id BIGINT PRIMARY KEY, " +
                "platform VARCHAR(50), " +
                "shop_id VARCHAR(100), " +
                "platform_order_no VARCHAR(100), " +
                "internal_order_no VARCHAR(100), " +
                "buyer_id VARCHAR(100), " +
                "buyer_name VARCHAR(200), " +
                "buyer_email VARCHAR(200), " +
                "total_amount DECIMAL(10,2), " +
                "currency VARCHAR(10), " +
                "status VARCHAR(50), " +
                "tracking_number VARCHAR(100), " +
                "logistics_company VARCHAR(100), " +
                "shipping_time TIMESTAMP, " +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
            jdbcTemplate.execute(ordersSql);
            
            // 创建产品表
            String productsSql = "CREATE TABLE IF NOT EXISTS " + schemaName + ".products (" +
                "id BIGINT PRIMARY KEY, " +
                "platform VARCHAR(50), " +
                "shop_id VARCHAR(100), " +
                "platform_product_id VARCHAR(100), " +
                "sku VARCHAR(100), " +
                "name VARCHAR(500), " +
                "description TEXT, " +
                "category VARCHAR(100), " +
                "brand VARCHAR(100), " +
                "price DECIMAL(10,2), " +
                "cost DECIMAL(10,2), " +
                "weight DECIMAL(10,2), " +
                "stock_quantity INT, " +
                "status VARCHAR(50), " +
                "image_urls TEXT, " +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
            jdbcTemplate.execute(productsSql);
            
            // 创建库存表
            String inventorySql = "CREATE TABLE IF NOT EXISTS " + schemaName + ".inventory (" +
                "id BIGINT PRIMARY KEY, " +
                "product_id BIGINT, " +
                "warehouse_id VARCHAR(100), " +
                "location VARCHAR(100), " +
                "quantity INT, " +
                "available_quantity INT, " +
                "reserved_quantity INT, " +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
            jdbcTemplate.execute(inventorySql);

            log.info("租户Schema创建成功: {}", schemaName);
        } catch (Exception e) {
            log.error("创建租户Schema失败: {}", e.getMessage());
            throw new RuntimeException("创建租户数据库失败", e);
        }
    }

    /**
     * 删除租户Schema
     */
    @Transactional
    public void deleteTenantSchema(String tenantId) {
        String schemaName = "tenant_" + tenantId.replace("-", "_");
        try {
            jdbcTemplate.execute("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE");
            log.info("租户Schema删除成功: {}", schemaName);
        } catch (Exception e) {
            log.error("删除租户Schema失败: {}", e.getMessage());
            throw new RuntimeException("删除租户数据库失败", e);
        }
    }

    /**
     * 获取租户Schema名称
     */
    public String getSchemaName(String tenantId) {
        return "tenant_" + tenantId.replace("-", "_");
    }
}