package com.crossborder.erp.warehouse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.warehouse.entity.Warehouse;
import com.crossborder.erp.warehouse.entity.InboundOrder;
import com.crossborder.erp.warehouse.entity.InboundDetail;
import com.crossborder.erp.warehouse.service.WarehouseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 仓库服务单元测试
 */
@SpringBootTest
public class WarehouseServiceTest {

    @Autowired
    private WarehouseService warehouseService;

    @Test
    public void testCreateWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setName("测试仓库");
        warehouse.setCode("TEST001");
        warehouse.setAddress("测试地址");
        warehouse.setManager("测试管理员");
        warehouse.setPhone("13800138000");

        Long warehouseId = warehouseService.createWarehouse(warehouse);
        assertNotNull(warehouseId);
        assertTrue(warehouseId > 0);

        Warehouse created = warehouseService.getWarehouse(warehouseId);
        assertEquals("测试仓库", created.getName());
    }

    @Test
    public void testListWarehouses() {
        var warehouses = warehouseService.listWarehouses();
        assertNotNull(warehouses);
        assertFalse(warehouses.isEmpty());
    }

    @Test
    public void testCreateInboundOrder() {
        InboundOrder order = new InboundOrder();
        order.setWarehouseId(1L);
        order.setSupplier("测试供应商");
        order.setInboundType("PURCHASE"); // 采购入库
        order.setRemark("测试入库单");

        InboundDetail detail = new InboundDetail();
        detail.setProductId(1L);
        detail.setQuantity(100);
        detail.setUnitPrice(10.0);
        detail.setLocationCode("A-01-01");

        Long orderId = warehouseService.createInboundOrder(
                order, Arrays.asList(detail)
        );
        
        assertNotNull(orderId);
        assertTrue(orderId > 0);
    }

    @Test
    public void testListInboundOrders() {
        Page<InboundOrder> page = warehouseService.listInboundOrders(1L, 1, 10);
        assertNotNull(page);
        assertNotNull(page.getRecords());
    }

    @Test
    public void testUpdateWarehouse() {
        Warehouse warehouse = warehouseService.getWarehouse(1L);
        assertNotNull(warehouse);

        warehouse.setManager("新管理员");
        warehouseService.updateWarehouse(warehouse);

        Warehouse updated = warehouseService.getWarehouse(1L);
        assertEquals("新管理员", updated.getManager());
    }
}
