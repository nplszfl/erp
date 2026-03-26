package com.crossborder.pricing.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 产品成本信息DTO
 */
@Data
public class ProductCostInfo {
    /** 产品ID */
    private Long productId;
    
    /** 物料成本 */
    private BigDecimal materialCost;
    
    /** 人工成本 */
    private BigDecimal laborCost;
    
    /** 间接成本 */
    private BigDecimal overheadCost;
    
    /** 运输成本 */
    private BigDecimal shippingCost;
    
    /** 总成本 */
    private BigDecimal totalCost;
    
    /** 其他成本 */
    private BigDecimal otherCost;
    
    /** 成本更新时间 */
    private java.time.LocalDateTime updateTime;
}