package com.crossborder.erp.inventory.alert.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存预警规则
 */
@Data
@TableName("t_inventory_alert_rule")
public class InventoryAlertRule {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品SKU
     */
    private String sku;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 仓库ID
     */
    private String warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 安全库存数量
     */
    private Integer safeStock;

    /**
     * 预警库存数量（低于此值触发预警）
     */
    private Integer alertStock;

    /**
     * 最低库存数量（缺货阈值，低于此值触发缺货预警）
     */
    private Integer minStock;

    /**
     * 库存类型（SAFE/LOW/OUT）
     */
    private String alertType;

    /**
     * 预警提前天数（提前N天预警）
     */
    private Integer advanceDays;

    /**
     * 通知方式（EMAIL/SMS/SYSTEM/ALL）
     */
    private String notifyType;

    /**
     * 通知人（多个用逗号分隔）
     */
    private String notifyTo;

    /**
     * 邮件通知列表（JSON数组）
     */
    private String emailList;

    /**
     * 手机号列表（JSON数组）
     */
    private String phoneList;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建人名称
     */
    private String creatorName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}