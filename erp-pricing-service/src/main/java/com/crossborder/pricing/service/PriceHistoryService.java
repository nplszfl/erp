package com.crossborder.pricing.service;

import com.crossborder.pricing.entity.PriceHistory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 价格历史服务
 */
public interface PriceHistoryService {

    /**
     * 记录价格变更
     */
    void recordPriceChange(PriceHistory history);

    /**
     * 获取产品价格历史
     */
    List<PriceHistory> getPriceHistory(Long productId);

    /**
     * 获取指定时间范围的价格历史
     */
    List<PriceHistory> getPriceHistory(Long productId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取最近N次价格变更
     */
    List<PriceHistory> getRecentPriceChanges(Long productId, int limit);

    /**
     * 获取价格趋势分析
     */
    PriceTrendAnalysis getPriceTrendAnalysis(Long productId, int days);

    /**
     * 价格趋势分析结果
     */
    class PriceTrendAnalysis {
        private Long productId;
        private BigDecimal startingPrice;
        private BigDecimal endingPrice;
        private BigDecimal priceChangeAmount;
        private BigDecimal priceChangePercent;
        private Long totalChanges;
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        // Getters and Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public BigDecimal getStartingPrice() { return startingPrice; }
        public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }

        public BigDecimal getEndingPrice() { return endingPrice; }
        public void setEndingPrice(BigDecimal endingPrice) { this.endingPrice = endingPrice; }

        public BigDecimal getPriceChangeAmount() { return priceChangeAmount; }
        public void setPriceChangeAmount(BigDecimal priceChangeAmount) { this.priceChangeAmount = priceChangeAmount; }

        public BigDecimal getPriceChangePercent() { return priceChangePercent; }
        public void setPriceChangePercent(BigDecimal priceChangePercent) { this.priceChangePercent = priceChangePercent; }

        public Long getTotalChanges() { return totalChanges; }
        public void setTotalChanges(Long totalChanges) { this.totalChanges = totalChanges; }

        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    }
}
