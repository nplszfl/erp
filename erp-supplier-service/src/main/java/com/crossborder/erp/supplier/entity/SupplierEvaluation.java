package com.crossborder.erp.supplier.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 供应商评估记录实体
 */
@Data
@TableName("t_supplier_evaluation")
public class SupplierEvaluation {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 评估维度 - 产品质量 (1-5)
     */
    private Integer qualityScore;

    /**
     * 评估维度 - 交付准时率 (1-5)
     */
    private Integer deliveryScore;

    /**
     * 评估维度 - 价格竞争力 (1-5)
     */
    private Integer priceScore;

    /**
     * 评估维度 - 服务响应 (1-5)
     */
    private Integer serviceScore;

    /**
     * 评估维度 - 技术能力 (1-5)
     */
    private Integer techScore;

    /**
     * 综合评分 (1-5)
     */
    private BigDecimal totalScore;

    /**
     * 评估结论（EXCELLENT/GOOD/ACCEPTABLE/POOR）
     */
    private String conclusion;

    /**
     * 优点
     */
    private String advantages;

    /**
     * 待改进项
     */
    private String improvements;

    /**
     * 评估备注
     */
    private String remark;

    /**
     * 评估人ID
     */
    private Long evaluatorId;

    /**
     * 评估人姓名
     */
    private String evaluatorName;

    /**
     * 评估时间
     */
    private LocalDateTime evaluateAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}