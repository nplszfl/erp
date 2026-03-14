package com.crossborder.erp.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.common.exception.BusinessException;
import com.crossborder.erp.warehouse.entity.*;
import com.crossborder.erp.warehouse.enums.InboundType;
import com.crossborder.erp.warehouse.enums.OutboundType;
import com.crossborder.erp.warehouse.mapper.*;
import com.crossborder.erp.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 仓库服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseMapper warehouseMapper;
    private final WarehouseLocationMapper locationMapper;
    private final InboundOrderMapper inboundOrderMapper;
    private final InboundDetailMapper inboundDetailMapper;
    private final OutboundOrderMapper outboundOrderMapper;
    private final OutboundDetailMapper outboundDetailMapper;
    private final StockTransferMapper transferMapper;

    @Override
    public Warehouse getWarehouse(Long warehouseId) {
        return warehouseMapper.selectById(warehouseId);
    }

    @Override
    public List<Warehouse> listWarehouses() {
        return warehouseMapper.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWarehouse(Warehouse warehouse) {
        warehouse.setCreateTime(LocalDateTime.now());
        warehouse.setUpdateTime(LocalDateTime.now());
        warehouse.setStatus(1);
        warehouseMapper.insert(warehouse);
        log.info("创建仓库成功: {}", warehouse.getName());
        return warehouse.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWarehouse(Warehouse warehouse) {
        warehouse.setUpdateTime(LocalDateTime.now());
        warehouseMapper.updateById(warehouse);
        log.info("更新仓库成功: {}", warehouse.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWarehouse(Long warehouseId) {
        // 检查仓库是否有库存
        LambdaQueryWrapper<WarehouseLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarehouseLocation::getWarehouseId, warehouseId)
               .gt(WarehouseLocation::getStockQuantity, 0);

        Long stockCount = locationMapper.selectCount(wrapper);
        if (stockCount > 0) {
            throw new BusinessException("仓库有库存，无法删除");
        }

        warehouseMapper.deleteById(warehouseId);
        log.info("删除仓库成功: ID={}", warehouseId);
    }

    @Override
    public Page<WarehouseLocation> listLocations(Long warehouseId, int page, int size) {
        Page<WarehouseLocation> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<WarehouseLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarehouseLocation::getWarehouseId, warehouseId);
        return locationMapper.selectPage(pageObj, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInboundOrder(InboundOrder order, List<InboundDetail> details) {
        order.setOrderNo(generateInboundOrderNo());
        order.setStatus(0); // 待入库
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        inboundOrderMapper.insert(order);

        for (InboundDetail detail : details) {
            detail.setInboundOrderId(order.getId());
            detail.setCreateTime(LocalDateTime.now());
            inboundDetailMapper.insert(detail);
        }

        log.info("创建入库单成功: {}", order.getOrderNo());
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmInbound(Long inboundOrderId) {
        InboundOrder order = inboundOrderMapper.selectById(inboundOrderId);
        if (order == null) {
            throw new BusinessException("入库单不存在");
        }

        if (order.getStatus() == 1) {
            throw new BusinessException("入库单已入库");
        }

        // 获取入库明细
        LambdaQueryWrapper<InboundDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InboundDetail::getInboundOrderId, inboundOrderId);
        List<InboundDetail> details = inboundDetailMapper.selectList(wrapper);

        // 更新库存
        for (InboundDetail detail : details) {
            updateLocationStock(order.getWarehouseId(), detail.getProductId(),
                             detail.getQuantity(), detail.getLocationCode());
        }

        // 更新入库单状态
        order.setStatus(1); // 已入库
        order.setInboundTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        inboundOrderMapper.updateById(order);

        log.info("确认入库成功: {}", order.getOrderNo());
    }

    @Override
    public Page<InboundOrder> listInboundOrders(Long warehouseId, int page, int size) {
        Page<InboundOrder> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<InboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InboundOrder::getWarehouseId, warehouseId)
               .orderByDesc(InboundOrder::getCreateTime);
        return inboundOrderMapper.selectPage(pageObj, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOutboundOrder(OutboundOrder order, List<OutboundDetail> details) {
        order.setOrderNo(generateOutboundOrderNo());
        order.setStatus(0); // 待出库
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        outboundOrderMapper.insert(order);

        for (OutboundDetail detail : details) {
            detail.setOutboundOrderId(order.getId());
            detail.setCreateTime(LocalDateTime.now());
            outboundDetailMapper.insert(detail);
        }

        log.info("创建出库单成功: {}", order.getOrderNo());
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOutbound(Long outboundOrderId) {
        OutboundOrder order = outboundOrderMapper.selectById(outboundOrderId);
        if (order == null) {
            throw new BusinessException("出库单不存在");
        }

        if (order.getStatus() == 1) {
            throw new BusinessException("出库单已出库");
        }

        // 获取出库明细
        LambdaQueryWrapper<OutboundDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutboundDetail::getOutboundOrderId, outboundOrderId);
        List<OutboundDetail> details = outboundDetailMapper.selectList(wrapper);

        // 扣减库存
        for (OutboundDetail detail : details) {
            reduceLocationStock(order.getWarehouseId(), detail.getProductId(),
                             detail.getQuantity(), detail.getLocationCode());
        }

        // 更新出库单状态
        order.setStatus(1); // 已出库
        order.setOutboundTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        outboundOrderMapper.updateById(order);

        log.info("确认出库成功: {}", order.getOrderNo());
    }

    @Override
    public Page<OutboundOrder> listOutboundOrders(Long warehouseId, int page, int size) {
        Page<OutboundOrder> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutboundOrder::getWarehouseId, warehouseId)
               .orderByDesc(OutboundOrder::getCreateTime);
        return outboundOrderMapper.selectPage(pageObj, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTransfer(StockTransfer transfer) {
        transfer.setTransferNo(generateTransferNo());
        transfer.setStatus(0); // 待调拨
        transfer.setCreateTime(LocalDateTime.now());
        transfer.setUpdateTime(LocalDateTime.now());
        transferMapper.insert(transfer);

        log.info("创建调拨单成功: {}", transfer.getTransferNo());
        return transfer.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeTransfer(Long transferId) {
        StockTransfer transfer = transferMapper.selectById(transferId);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }

        if (transfer.getStatus() == 1) {
            throw new BusinessException("调拨单已调拨");
        }

        // 从源仓库扣减库存
        reduceLocationStock(transfer.getFromWarehouseId(), transfer.getProductId(),
                         transfer.getQuantity(), transfer.getFromLocationCode());

        // 向目标仓库增加库存
        updateLocationStock(transfer.getToWarehouseId(), transfer.getProductId(),
                         transfer.getQuantity(), transfer.getToLocationCode());

        // 更新调拨单状态
        transfer.setStatus(1); // 已调拨
        transfer.setTransferTime(LocalDateTime.now());
        transfer.setUpdateTime(LocalDateTime.now());
        transferMapper.updateById(transfer);

        log.info("调拨成功: {}", transfer.getTransferNo());
    }

    /**
     * 更新库位库存
     */
    private void updateLocationStock(Long warehouseId, Long productId,
                                  Integer quantity, String locationCode) {
        LambdaQueryWrapper<WarehouseLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarehouseLocation::getWarehouseId, warehouseId)
               .eq(WarehouseLocation::getProductId, productId)
               .eq(WarehouseLocation::getLocationCode, locationCode);

        WarehouseLocation location = locationMapper.selectOne(wrapper);

        if (location == null) {
            // 创建新库位
            location = new WarehouseLocation();
            location.setWarehouseId(warehouseId);
            location.setProductId(productId);
            location.setLocationCode(locationCode);
            location.setStockQuantity(quantity);
            location.setLockedQuantity(0);
            location.setCreateTime(LocalDateTime.now());
            location.setUpdateTime(LocalDateTime.now());
            locationMapper.insert(location);
        } else {
            // 更新库存
            location.setStockQuantity(location.getStockQuantity() + quantity);
            location.setUpdateTime(LocalDateTime.now());
            locationMapper.updateById(location);
        }
    }

    /**
     * 扣减库位库存
     */
    private void reduceLocationStock(Long warehouseId, Long productId,
                                   Integer quantity, String locationCode) {
        LambdaQueryWrapper<WarehouseLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarehouseLocation::getWarehouseId, warehouseId)
               .eq(WarehouseLocation::getProductId, productId)
               .eq(WarehouseLocation::getLocationCode, locationCode);

        WarehouseLocation location = locationMapper.selectOne(wrapper);

        if (location == null) {
            throw new BusinessException("库位不存在");
        }

        if (location.getStockQuantity() < quantity) {
            throw new BusinessException("库存不足，当前库存: " + location.getStockQuantity());
        }

        location.setStockQuantity(location.getStockQuantity() - quantity);
        location.setUpdateTime(LocalDateTime.now());
        locationMapper.updateById(location);
    }

    /**
     * 生成入库单号
     */
    private String generateInboundOrderNo() {
        return "IN" + System.currentTimeMillis();
    }

    /**
     * 生成出库单号
     */
    private String generateOutboundOrderNo() {
        return "OUT" + System.currentTimeMillis();
    }

    /**
     * 生成调拨单号
     */
    private String generateTransferNo() {
        return "TF" + System.currentTimeMillis();
    }
}
