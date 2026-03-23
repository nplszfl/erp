package com.crossborder.erp.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crossborder.erp.order.entity.Order;
import com.crossborder.erp.order.entity.OrderItem;
import com.crossborder.erp.order.mapper.OrderMapper;
import com.crossborder.erp.order.mapper.OrderItemMapper;
import com.crossborder.erp.order.service.OrderService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务实现（带监控）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderItemMapper orderItemMapper;

    @Override
    public long count(LambdaQueryWrapper<Order> wrapper) {
        return super.count(wrapper);
    }

    @Override
    @Timed(value = "order.create", description = "创建订单", histogram = true)
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Order order, List<OrderItem> orderItems) {
        log.info("创建订单, 平台订单号: {}", order.getPlatformOrderNo());

        // 生成内部订单号
        String internalOrderNo = generateInternalOrderNo(order.getPlatform());
        order.setInternalOrderNo(internalOrderNo);

        // 保存订单
        save(order);

        // 保存订单商品明细
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        log.info("订单创建成功, 内部订单号: {}, 商品数量: {}", internalOrderNo, orderItems.size());
        return order.getId();
    }

    @Override
    @Timed(value = "order.getById", description = "根据ID查询订单")
    public Order getOrderById(Long orderId) {
        return getById(orderId);
    }

    @Override
    @Timed(value = "order.getByPlatformOrderNo", description = "根据平台订单号查询")
    public Order getOrderByPlatformOrderNo(String platform, String platformOrderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getPlatform, platform)
                .eq(Order::getPlatformOrderNo, platformOrderNo);
        return getOne(wrapper);
    }

    @Override
    @Timed(value = "order.updateStatus", description = "更新订单状态")
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Long orderId, String status) {
        log.info("更新订单状态, orderId: {}, status: {}", orderId, status);

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(status);
        updateById(order);
    }

    @Override
    @Timed(value = "order.updateShippingInfo", description = "更新物流信息")
    @Transactional(rollbackFor = Exception.class)
    public void updateShippingInfo(Long orderId, String trackingNumber, String logisticsCompany) {
        log.info("更新订单物流信息, orderId: {}, trackingNumber: {}", orderId, trackingNumber);

        Order order = new Order();
        order.setId(orderId);
        order.setTrackingNumber(trackingNumber);
        order.setLogisticsCompany(logisticsCompany);
        order.setShippingTime(LocalDateTime.now());
        order.setStatus("shipped");
        updateById(order);
    }

    @Override
    @Timed(value = "order.pageOrders", description = "分页查询订单")
    public IPage<Order> pageOrders(Page<Order> page, String platform, String status,
                                   LocalDateTime startTime,
                                   LocalDateTime endTime) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (platform != null) {
            wrapper.eq(Order::getPlatform, platform);
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (startTime != null) {
            wrapper.ge(Order::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(Order::getCreateTime, endTime);
        }

        wrapper.orderByDesc(Order::getCreateTime);

        return page(page, wrapper);
    }

    @Override
    @Timed(value = "order.getOrderItems", description = "查询订单商品")
    public List<OrderItem> getOrderItems(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        return orderItemMapper.selectList(wrapper);
    }

    /**
     * 生成内部订单号
     * 格式：平台代码 + 时间戳 + 后缀
     */
    private String generateInternalOrderNo(String platform) {
        long timestamp = System.currentTimeMillis();
        String suffix = String.format("%04d", (int)(Math.random() * 10000));
        return platform.toUpperCase() + timestamp + suffix;
    }
}
