package com.crossborder.erp.platform.service;

import com.crossborder.erp.common.constant.PlatformType;
import com.crossborder.erp.order.entity.Order;
import com.crossborder.erp.order.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 平台订单同步服务接口
 */
public interface PlatformOrderSyncService {

    /**
     * 同步指定平台的订单
     * @param platform 平台类型
     * @param shopId 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    List<Order> syncOrders(String platform, String shopId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取平台订单详情
     * @param platform 平台类型
     * @param platformOrderNo 平台订单号
     * @param shopId 店铺ID
     * @return 订单详情
     */
    Order getOrderByNo(String platform, String platformOrderNo, String shopId);

    /**
     * 验证平台API配置
     * @param platform 平台类型
     * @param shopId 店铺ID
     * @return true有效
     */
    boolean validateConfig(String platform, String shopId);

    /**
     * 支持的的平台列表
     * @return 平台列表
     */
    List<PlatformType> getSupportedPlatforms();
}
