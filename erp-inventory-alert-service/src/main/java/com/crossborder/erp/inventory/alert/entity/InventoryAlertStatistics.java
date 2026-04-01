package com.crossborder.erp.inventory.alert.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存预警统计
 */
@Data
@TableName("t_inventory_alert_statistics")
public class InventoryAlertStatistics {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 统计日期
     */
    private LocalDate statisticsDate;

    /**
     * 仓库ID
     */
    private String warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 总预警数
     */
    private Integer totalAlertCount;

    /**
     * 待处理数
     */
    private Integer pendingCount;

    /**
     * 已处理数
     */
    private Integer handledCount;

    /**
     * 已忽略数
     */
    private Integer ignoredCount;

    /**
     * 低库存预警数
     */
    private Integer lowStockCount;

    /**
     * 缺货预警数
     */
    private Integer outOfStockCount;

    /**
     * 安全库存警告数
     */
    private Integer safeStockWarningCount;

    /**
     * 预警处理率
     */
    private Double handleRate;

    /**
     * 平均处理时长（分钟）
     */
    private Double avgHandleDuration;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}