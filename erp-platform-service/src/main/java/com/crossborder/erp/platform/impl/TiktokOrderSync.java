package com.crossborder.erp.platform.impl;

import com.crossborder.erp.common.constant.PlatformType;
import com.crossborder.erp.order.entity.Order;
import com.crossborder.erp.order.entity.OrderItem;
import com.crossborder.erp.platform.api.PlatformOrderSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TikTok Shop订单同步实现
 * 使用TikTok Shop Open Platform API
 */
@Slf4j
@Component
public class TiktokOrderSync implements PlatformOrderSync {

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.TIKTOK;
    }

    @Override
    public List<Order> syncOrders(LocalDateTime startTime, LocalDateTime endTime, String shopId) {
        log.info("开始同步TikTok Shop订单, 店铺: {}, 时间范围: {} - {}", shopId, startTime, endTime);

        // TODO: 调用TikTok Shop Open API获取订单列表
        // 文档：https://partner.tiktokshop.com/doc/v2/order/query-orders.html

        List<Order> orders = new ArrayList<>();

        // 1. 构建API请求
        // 2. 调用 /api/v2/order/query_orders
        // 3. 处理分页
        // 4. 转换为Order对象

        log.info("TikTok Shop订单同步完成, 共获取 {} 条订单", orders.size());
        return orders;
    }

    @Override
    public Order getOrderByNo(String platformOrderNo, String shopId) {
        log.info("获取TikTok Shop订单详情, 订单号: {}", platformOrderNo);

        // TODO: 调用TikTok Shop API获取订单详情
        // 接口：/api/v2/order/query_orders

        return null;
    }

    @Override
    public List<OrderItem> syncOrderItems(String platformOrderNo, String shopId) {
        log.info("同步TikTok Shop订单商品明细, 订单号: {}", platformOrderNo);

        // TODO: 调用TikTok Shop API获取订单商品明细
        // 订单商品明细包含在GetOrders响应中

        return new ArrayList<>();
    }

    @Override
    public boolean validateConfig(String shopId) {
        log.info("验证TikTok Shop API配置, 店铺: {}", shopId);

        // TODO: 调用TikTok Shop API验证配置有效性
        // 可以调用任何需要认证的接口验证

        return true;
    }
}
