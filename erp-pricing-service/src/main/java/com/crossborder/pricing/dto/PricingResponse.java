package com.crossborder.pricing.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 定价响应DTO
 */
@Data
public class PricingResponse {

    /** 产品ID */
    private Long productId;

    /** 产品编码 */
    private String productCode;

    /** 原价格 */
    private BigDecimal originalPrice;

    /** 推荐价格 */
    private BigDecimal recommendedPrice;

    /** 价格变动（%） */
    private BigDecimal priceChangePercent;

    /** 预期利润率（%） */
    private BigDecimal profitMargin;

    /** 竞品平均价格 */
    private BigDecimal competitorAvgPrice;

    /** 竞品最低价格 */
    private BigDecimal competitorMinPrice;

    /** 竞品最高价格 */
    private BigDecimal competitorMaxPrice;

    /** 价格策略说明 */
    private String pricingStrategy;

    /** 调整原因 */
    private String adjustmentReason;

    /** 因子贡献（季节性、库存、需求等） */
    private Map<String, BigDecimal> factorContributions;

    /** 是否应用成功 */
    private Boolean applied;

    /** 计算时间 */
    private LocalDateTime calculationTime;
}
