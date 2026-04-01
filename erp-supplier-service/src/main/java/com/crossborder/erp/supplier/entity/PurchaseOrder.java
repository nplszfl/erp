package com.crossborder.erp.supplier.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购订单实体
 */
@Data
@TableName("t_purchase_order")
public class PurchaseOrder {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 采购单号
     */
    private String orderNo;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商名称（冗余）
     */
    private String supplierName;

    /**
     * 订单状态（PENDING/APPROVED/PURCHASING/IN_TRANSIT/RECEIVED/CANCELLED）
     */
    private String status;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 已付金额
     */
    private BigDecimal paidAmount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 预计到货日期
     */
    private LocalDateTime expectedArrivalAt;

    /**
     * 实际到货日期
     */
    private LocalDateTime actualArrivalAt;

    /**
     * 付款方式（TT/LC/DP/DA）
     */
    private String paymentMethod;

    /**
     * 付款状态（UNPAID/PARTIAL/PAID）
     */
    private String paymentStatus;

    /**
     * 采购员ID
     */
    private Long purchaserId;

    /**
     * 采购员姓名
     */
    private String purchaserName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 订单明细（非数据库字段）
     */
    @TableField(exist = false)
    private List<PurchaseOrderItem> items;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}