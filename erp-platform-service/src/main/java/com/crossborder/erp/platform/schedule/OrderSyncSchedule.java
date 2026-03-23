package com.crossborder.erp.platform.schedule;

import com.crossborder.erp.order.entity.Order;
import com.crossborder.erp.order.entity.OrderItem;
import com.crossborder.erp.platform.api.PlatformOrderSync;
import com.crossborder.erp.platform.entity.PlatformConfig;
import com.crossborder.erp.platform.service.PlatformConfigService;
import com.crossborder.erp.order.service.OrderService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单同步调度器（带监控）
 * 定时从各平台拉取新订单
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSyncSchedule {

    private final PlatformConfigService platformConfigService;
    private final Map<String, PlatformOrderSync> platformSyncMap;
    private final OrderService orderService;

    /**
     * 获取所有平台的同步实现（包含过滤后的bean）
     */
    private Map<String, PlatformOrderSync> getPlatformSyncMap() {
        return platformSyncMap.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 每10分钟同步一次订单
     */
    @Scheduled(cron = "0 */10 * * * ?")
    @Timed(value = "sync.orders", description = "定时同步订单", histogram = true)
    public void syncOrders() {
        log.info("开始定时同步订单...");

        List<PlatformConfig> configs = platformConfigService.getAllConfigs();
        configs.stream()
                .filter(config -> config.getStatus() == 1) // 只同步启用的配置
                .forEach(this::syncPlatformOrders);

        log.info("订单同步完成");
    }

    /**
     * 同步单个平台的订单
     */
    @Timed(value = "sync.platform.orders", description = "同步平台订单")
    private void syncPlatformOrders(PlatformConfig config) {
        try {
            String platform = config.getPlatform();
            String shopId = config.getShopId();

            PlatformOrderSync sync = platformSyncMap.get(platform.toLowerCase());
            if (sync == null) {
                log.warn("暂不支持平台: {}", platform);
                return;
            }

            // 同步最近1小时的订单
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = endTime.minusHours(1);

            log.info("同步平台订单: platform={}, shopId={}, time={}", platform, shopId, startTime);

            List<Order> orders = sync.syncOrders(startTime, endTime, shopId);

            for (Order order : orders) {
                try {
                    // 查询订单商品明细
                    List<OrderItem> items = sync.syncOrderItems(order.getPlatformOrderNo(), shopId);

                    // 调用订单服务保存订单
                    saveOrderToOrderService(order, items);
                } catch (Exception e) {
                    log.error("保存订单失败: {}", order.getPlatformOrderNo(), e);
                }
            }

            log.info("平台订单同步完成: platform={}, count={}", platform, orders.size());

        } catch (Exception e) {
            log.error("同步平台订单失败: {}", config.getShopName(), e);
        }
    }

    /**
     * 手动触发单个平台订单同步
     * @param platform 平台类型
     * @param shopId 店铺ID
     */
    public void syncPlatformOrders(String platform, String shopId) {
        log.info("手动触发订单同步: platform={}, shopId={}", platform, shopId);
        
        // 创建一个虚拟的 PlatformConfig 用于调用
        PlatformConfig config = new PlatformConfig();
        config.setPlatform(platform);
        config.setShopId(shopId);
        config.setShopName("手动触发");
        config.setStatus(1);
        
        syncPlatformOrders(config);
    }

    /**
     * 保存订单到订单服务
     */
    @Timed(value = "sync.save.order", description = "保存订单到订单服务")
    private void saveOrderToOrderService(Order order, List<OrderItem> items) {
        try {
            // 检查订单是否已存在
            Order existingOrder = orderService.getOrderByPlatformOrderNo(order.getPlatform(), order.getPlatformOrderNo());
            if (existingOrder != null) {
                log.info("订单已存在，跳过: {}", order.getPlatformOrderNo());
                return;
            }
            
            // 调用订单服务保存订单
            Long orderId = orderService.createOrder(order, items);
            log.info("订单保存成功: platformOrderNo={}, orderId={}", order.getPlatformOrderNo(), orderId);
        } catch (Exception e) {
            log.error("保存订单失败: {}", order.getPlatformOrderNo(), e);
        }
    }
}
