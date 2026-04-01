package com.crossborder.erp.inventory.alert.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存预警记录
 */
@Data
@TableName("t_inventory_alert")
public class InventoryAlert {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 预警规则ID
     */
    private Long ruleId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * SKU
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
     * 当前库存
     */
    private Integer currentStock;

    /**
     * 预警库存值
     */
    private Integer alertStock;

    /**
     * 安全库存值
     */
    private Integer safeStock;

    /**
     * 预警类型：LOW(库存偏低)、OUT(缺货)、SAFE(安全库存警告)
     */
    private String alertType;

    /**
     * 预警消息
     */
    private String message;

    /**
     * 状态：PENDING(待处理)、HANDLED(已处理)、IGNORED(已忽略)
     */
    private String status;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 处理人名称
     */
    private String handlerName;

    /**
     * 处理备注
     */
    private String handleRemark;

    /**
     * 是否已发送通知
     */
    private Boolean notified;

    /**
     * 通知方式
     */
    private String notifyType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private LocalDateTime handledTime;
}