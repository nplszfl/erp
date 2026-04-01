package com.crossborder.erp.supplier.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购订单明细实体
 */
@Data
@TableName("t_purchase_order_item")
public class PurchaseOrderItem {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 采购订单ID
     */
    private Long orderId;

    /**
     * 产品SKU
     */
    private String sku;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品类别
     */
    private String category;

    /**
     * 采购数量
     */
    private Integer quantity;

    /**
     * 单位
     */
    private String unit;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 小计金额
     */
    private BigDecimal subtotal;

    /**
     * 已到货数量
     */
    private Integer receivedQuantity;

    /**
     * 备注
     */
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}