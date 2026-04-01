package com.crossborder.erp.supplier.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.supplier.entity.PurchaseOrder;
import com.crossborder.erp.supplier.entity.PurchaseOrderItem;
import com.crossborder.erp.supplier.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购订单管理控制器
 */
@RestController
@RequestMapping("/supplier/purchase")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    /**
     * 创建采购订单
     */
    @PostMapping("/create")
    public Long create(@RequestBody PurchaseOrderRequest request) {
        return purchaseOrderService.createOrder(request.getOrder(), request.getItems());
    }

    /**
     * 获取订单详情（包含明细）
     */
    @GetMapping("/{id}")
    public PurchaseOrder get(@PathVariable Long id) {
        return purchaseOrderService.getOrderWithItems(id);
    }

    /**
     * 分页查询采购订单
     */
    @GetMapping("/list")
    public IPage<PurchaseOrder> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus) {
        Page<PurchaseOrder> page = new Page<>(current, size);
        return purchaseOrderService.pageOrders(page, orderNo, supplierId, status, paymentStatus);
    }

    /**
     * 获取订单明细
     */
    @GetMapping("/{id}/items")
    public List<PurchaseOrderItem> getItems(@PathVariable Long id) {
        return purchaseOrderService.getItemsByOrderId(id);
    }

    /**
     * 更新采购订单
     */
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody PurchaseOrder order) {
        order.setId(id);
        purchaseOrderService.updateOrder(order);
    }

    /**
     * 确认订单
     */
    @PostMapping("/{id}/approve")
    public void approve(@PathVariable Long id) {
        purchaseOrderService.approveOrder(id);
    }

    /**
     * 标记发货
     */
    @PostMapping("/{id}/purchasing")
    public void markPurchasing(@PathVariable Long id) {
        purchaseOrderService.markPurchasing(id);
    }

    /**
     * 标记到货
     */
    @PostMapping("/{id}/received")
    public void markReceived(@PathVariable Long id) {
        purchaseOrderService.markReceived(id);
    }

    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        purchaseOrderService.cancelOrder(id, reason);
    }

    /**
     * 付款
     */
    @PostMapping("/{id}/payment")
    public void payment(@PathVariable Long id, @RequestParam BigDecimal amount) {
        purchaseOrderService.makePayment(id, amount);
    }

    /**
     * 添加订单明细
     */
    @PostMapping("/{id}/item")
    public void addItem(@PathVariable Long id, @RequestBody PurchaseOrderItem item) {
        item.setOrderId(id);
        purchaseOrderService.addOrderItem(item);
    }

    /**
     * 删除订单明细
     */
    @DeleteMapping("/item/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        purchaseOrderService.deleteOrderItem(itemId);
    }

    /**
     * 获取供应商的采购订单
     */
    @GetMapping("/supplier/{supplierId}")
    public List<PurchaseOrder> getBySupplierId(@PathVariable Long supplierId) {
        return purchaseOrderService.getBySupplierId(supplierId);
    }

    /**
     * 获取待收货订单
     */
    @GetMapping("/in-transit")
    public List<PurchaseOrder> getInTransit() {
        return purchaseOrderService.getInTransitOrders();
    }

    /**
     * 采购订单统计
     */
    @GetMapping("/stats")
    public PurchaseOrderService.OrderStats getStats() {
        return purchaseOrderService.getOrderStats();
    }

    /**
     * 删除采购订单
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        purchaseOrderService.deleteOrder(id);
    }

    /**
     * 请求体 - 创建订单
     */
    public static class PurchaseOrderRequest {
        private PurchaseOrder order;
        private List<PurchaseOrderItem> items;

        public PurchaseOrder getOrder() {
            return order;
        }

        public void setOrder(PurchaseOrder order) {
            this.order = order;
        }

        public List<PurchaseOrderItem> getItems() {
            return items;
        }

        public void setItems(List<PurchaseOrderItem> items) {
            this.items = items;
        }
    }
}