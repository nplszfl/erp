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
 * Lazada订单同步实现
 * 使用Lazada Open Platform API
 */
@Slf4j
@Component
public class LazadaOrderSync implements PlatformOrderSync {

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.LAZADA;
    }

    @Override
    public List<Order> syncOrders(LocalDateTime startTime, LocalDateTime endTime, String shopId) {
        log.info("开始同步Lazada订单, 店铺: {}, 时间范围: {} - {}", shopId, startTime, endTime);

        // TODO: 调用Lazada Open API获取订单列表
        // 接口：https://open.lazada.com/doc/doc.htm?spm=a2o9m.11193683.0.0.1f986635cO6cHs#/?nav=2&articleId=823

        List<Order> orders = new ArrayList<>();

        // 1. 获取订单列表 - GetOrders
        // 2. 分页处理
        // 3. 转换为Order对象

        log.info("Lazada订单同步完成, 共获取 {} 条订单", orders.size());
        return orders;
    }

    @Override
    public Order getOrderByNo(String platformOrderNo, String shopId) {
        log.info("获取Lazada订单详情, 订单号: {}", platformOrderNo);

        // TODO: 调用Lazada API获取订单详情
        // GetOrder接口

        return null;
    }

    @Override
    public List<OrderItem> syncOrderItems(String platformOrderNo, String shopId) {
        log.info("同步Lazada订单商品明细, 订单号: {}", platformOrderNo);

        // TODO: 调用Lazada API获取订单商品明细
        // GetOrderItems接口

        return new ArrayList<>();
    }

    @Override
    public boolean validateConfig(String shopId) {
        log.info("验证Lazada API配置, 店铺: {}", shopId);

        // TODO: 调用Lazada API验证配置有效性
        // 可以调用GetAccessToken接口验证

        return true;
    }
}
