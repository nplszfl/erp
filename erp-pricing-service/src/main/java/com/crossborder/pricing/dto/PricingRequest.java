package com.crossborder.pricing.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 定价请求DTO
 */
@Data
public class PricingRequest {

    /** 产品ID */
    private Long productId;

    /** 产品编码 */
    private String productCode;

    /** 成本价 */
    private BigDecimal costPrice;

    /** 当前售价 */
    private BigDecimal currentPrice;

    /** 目标利润率（%） */
    private BigDecimal targetProfitMargin;

    /** 平台ID */
    private Long platformId;

    /** 是否启用竞品分析 */
    private Boolean enableCompetitorAnalysis = true;

    /** 是否启用季节性调整 */
    private Boolean enableSeasonalAdjustment = true;

    /** 是否启用库存调整 */
    private Boolean enableInventoryAdjustment = true;
}
