package com.crossborder.pricing.service;

import com.crossborder.pricing.entity.CompetitorProduct;

import java.math.BigDecimal;
import java.util.List;

/**
 * 竞品数据抓取服务
 *
 * 功能：
 * 1. 从Amazon、eBay、Shopee等平台抓取竞品价格
 * 2. 解析HTML获取产品信息（价格、评分、评论数、销量）
 * 3. 存储到数据库
 * 4. 提供竞品分析数据
 */
public interface CompetitorScrapeService {

    /**
     * 抓取竞品数据
     */
    void scrapeCompetitorProducts(Long productId);

    /**
     * 从指定平台抓取竞品数据
     */
    void scrapeFromPlatform(String platform, String productUrl);

    /**
     * 批量抓取多个产品的竞品数据
     */
    void batchScrape(List<Long> productIds);

    /**
     * 获取产品的竞品数据
     */
    List<CompetitorProduct> getCompetitorProducts(Long productId);

    /**
     * 获取竞品价格统计（平均、最低、最高）
     */
    CompetitorPriceStats getCompetitorPriceStats(Long productId);

    /**
     * 手动添加竞品
     */
    void addCompetitor(Long productId, CompetitorProduct competitor);

    /**
     *   竞品价格统计
     */
    class CompetitorPriceStats {
        private Long productId;
        private BigDecimal avgPrice;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private Long competitorCount;

        // Getters and Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public BigDecimal getAvgPrice() { return avgPrice; }
        public void setAvgPrice(BigDecimal avgPrice) { this.avgPrice = avgPrice; }

        public BigDecimal getMinPrice() { return minPrice; }
        public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

        public BigDecimal getMaxPrice() { return maxPrice; }
        public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }

        public Long getCompetitorCount() { return competitorCount; }
        public void setCompetitorCount(Long competitorCount) { this.competitorCount = competitorCount; }
    }
}
