package com.crossborder.erp.order.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.order.entity.Order;
import com.crossborder.erp.order.entity.OrderItem;
import com.crossborder.erp.order.mapper.OrderMapper;
import com.crossborder.erp.order.mapper.OrderItemMapper;
import com.crossborder.erp.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 订单服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private List<OrderItem> testOrderItems;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testOrder = new Order();
        testOrder.setPlatform("amazon");
        testOrder.setShopId("US001");
        testOrder.setPlatformOrderNo("AMZ123456");
        testOrder.setBuyerName("John Doe");
        testOrder.setBuyerEmail("john@example.com");
        testOrder.setOrderAmount(new BigDecimal("99.99"));
        testOrder.setCurrencyCode("USD");
        testOrder.setStatus("pending_shipment");

        OrderItem item = new OrderItem();
        item.setPlatformSku("SKU001");
        item.setProductName("Test Product");
        item.setUnitPrice(new BigDecimal("99.99"));
        item.setQuantity(1);
        item.setTotalAmount(new BigDecimal("99.99"));

        testOrderItems = new ArrayList<>();
        testOrderItems.add(item);
    }

    @Test
    void testCreateOrder_Success() {
        // Given
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderItemMapper.insert(any(OrderItem.class))).thenReturn(1);

        // When
        Long orderId = orderService.createOrder(testOrder, testOrderItems);

        // Then
        assertNotNull(orderId);
        verify(orderMapper, times(1)).insert(any(Order.class));
        verify(orderItemMapper, times(1)).insert(any(OrderItem.class));
    }

    @Test
    void testGetOrderById_Success() {
        // Given
        testOrder.setId(1L);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        // When
        Order order = orderService.getOrderById(1L);

        // Then
        assertNotNull(order);
        assertEquals("AMZ123456", order.getPlatformOrderNo());
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Given
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        // When
        orderService.updateOrderStatus(1L, "shipped");

        // Then
        verify(orderMapper, times(1)).updateById(any(Order.class));
    }

    @Test
    void testUpdateShippingInfo_Success() {
        // Given
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        // When
        orderService.updateShippingInfo(1L, "TRACK001", "DHL");

        // Then
        verify(orderMapper, times(1)).updateById(any(Order.class));
    }

    @Test
    void testPageOrders_Success() {
        // Given
        Page<Order> page = new Page<>(1, 10);
        List<Order> orders = new ArrayList<>();
        orders.add(testOrder);

        when(orderMapper.selectPage(any(Page.class), any())).thenReturn(page);

        // When
        Page<Order> result = orderService.pageOrders(page, null, null, null, null);

        // Then
        assertNotNull(result);
        verify(orderMapper, times(1)).selectPage(any(Page.class), any());
    }

    @Test
    void testGetOrderItems_Success() {
        // Given
        testOrderItems.get(0).setOrderId(1L);
        when(orderItemMapper.selectList(any())).thenReturn(testOrderItems);

        // When
        List<OrderItem> items = orderService.getOrderItems(1L);

        // Then
        assertNotNull(items);
        assertEquals(1, items.size());
    }
}
