package com.crossborder.inventoryprediction.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 补货建议DTO
 */
@Data
public class ReplenishmentSuggestion {

    /** 产品ID */
    private Long productId;

    /** 产品SKU */
    private String productSku;

    /** 产品名称 */
    private String productName;

    /** 当前库存 */
    private Integer currentStock;

    /** 安全库存 */
    private Integer safetyStock;

    /** 日均销量 */
    private BigDecimal dailySales;

    /** 预测销量（未来N天） */
    private BigDecimal predictedSales;

    /** 预测需求总量 */
    private Long predictedDemand;

    /** 预测结束日期 */
    private LocalDate predictionEndDate;

    /** 建议补货量 */
    private Integer suggestedQuantity;

    /** 预计缺货日期 */
    private LocalDate expectedStockoutDate;

    /** 缺货风险（LOW/MEDIUM/HIGH） */
    private String stockoutRisk;

    /** 紧急程度 */
    private String urgency;

    /** 是否需要补货 */
    private Boolean needReplenishment;

    /** 预计到货日期 */
    private LocalDate expectedArrivalDate;

    /** 预测准确度 */
    private BigDecimal predictionAccuracy;

    /** 补货优先级（1-5，5最高） */
    private Integer priority;

    /** 建议补货日期 */
    private LocalDate suggestedReplenishmentDate;

    /** 建议日期（兼容） */
    private LocalDate suggestedDate;

    /** 补货原因 */
    private String reason;

    /** 预计补货后库存可支撑天数 */
    private Integer daysOfStock;

    /**
     * 风险评估内部类
     */
    @Data
    public static class RiskAssessment {
        /** 缺货风险 */
        private String stockoutRisk;
        /** 积压风险 */
        private String overstockRisk;
        /** 风险评分 */
        private BigDecimal riskScore;
    }
}