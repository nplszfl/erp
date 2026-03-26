package com.crossborder.pricing.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 竞品价格信息DTO
 */
@Data
public class CompetitorPriceInfo {
    /** 产品ID */
    private Long productId;
    
    /** 竞品名称 */
    private String competitorName;
    
    /** 最低价 */
    private BigDecimal lowestPrice;
    
    /** 最高价 */
    private BigDecimal highestPrice;
    
    /** 平均价 */
    private BigDecimal averagePrice;
    
    /** 中位数价格 */
    private BigDecimal medianPrice;
    
    /** 竞品数量 */
    private Integer competitorCount;
    
    /** 竞品价格列表 */
    private List<CompetitorPriceItem> prices;
    
    /** 数据更新时间 */
    private java.time.LocalDateTime updateTime;
    
    /**
     * 竞品价格项
     */
    @Data
    public static class CompetitorPriceItem {
        private String competitorName;
        private BigDecimal price;
        private String productUrl;
        private java.time.LocalDateTime scrapeTime;
    }
}