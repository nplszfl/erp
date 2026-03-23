package com.crossborder.pricing.service.impl;

import com.crossborder.pricing.entity.CompetitorProduct;
import com.crossborder.pricing.entity.PriceHistory;
import com.crossborder.pricing.service.CompetitorScrapeService;
import com.crossborder.pricing.service.DynamicPricingService;
import com.crossborder.pricing.service.PriceHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态定价服务实现
 * 基于竞品数据、库存、成本等因素自动计算最优价格
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicPricingServiceImpl implements DynamicPricingService {

    private final CompetitorScrapeService competitorScrapeService;
    private final PriceHistoryService priceHistoryService;

    /**
     * 定价策略参数
     */
    private static final BigDecimal MIN_MARGIN = new BigDecimal("0.05");  // 最小利润率5%
    private static final BigDecimal MAX_MARGIN = new BigDecimal("0.50");  // 最大利润率50%
    private static final BigDecimal COMPETITOR_WEIGHT = new BigDecimal("0.4");  // 竞品权重
    private static final BigDecimal COST_WEIGHT = new BigDecimal("0.3");   // 成本权重
    private static final BigDecimal DEMAND_WEIGHT = new BigDecimal("0.3"); // 需求权重

    @Override
    public BigDecimal calculateOptimalPrice(Long productId, BigDecimal costPrice, 
                                              Integer stockQuantity, Integer salesVelocity) {
        log.info("计算产品 {} 的最优价格", productId);

        // 1. 获取竞品数据
        List<CompetitorProduct> competitors = competitorScrapeService.getCompetitorProducts(productId);
        
        // 2. 获取历史价格趋势
        var priceTrend = priceHistoryService.getPriceTrendAnalysis(productId, 30);

        // 3. 计算各因素价格
        BigDecimal competitorBasedPrice = calculateCompetitorBasedPrice(competitors);
        BigDecimal costBasedPrice = calculateCostBasedPrice(costPrice);
        BigDecimal demandBasedPrice = calculateDemandBasedPrice(stockQuantity, salesVelocity, priceTrend);

        // 4. 加权计算最终价格
        BigDecimal optimalPrice = competitorBasedPrice.multiply(COMPETITOR_WEIGHT)
                .add(costBasedPrice.multiply(COST_WEIGHT))
                .add(demandBasedPrice.multiply(DEMAND_WEIGHT));

        // 5. 确保利润率在合理范围
        optimalPrice = ensureProfitable(optimalPrice, costPrice);

        log.info("产品 {} 最优价格: {}", productId, optimalPrice);
        return optimalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 基于竞品价格计算
     */
    private BigDecimal calculateCompetitorBasedPrice(List<CompetitorProduct> competitors) {
        if (competitors == null || competitors.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 计算竞品平均价格
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;

        for (CompetitorProduct competitor : competitors) {
            BigDecimal price = competitor.getCurrentPrice();
            totalPrice = totalPrice.add(price);
            
            if (minPrice == null || price.compareTo(minPrice) < 0) {
                minPrice = price;
            }
            if (maxPrice == null || price.compareTo(maxPrice) > 0) {
                maxPrice = price;
            }
        }

        BigDecimal avgPrice = totalPrice.divide(
                BigDecimal.valueOf(competitors.size()), 
                2, RoundingMode.HALF_UP
        );

        // 策略：在平均价格和最低价格之间选择有竞争力的价格
        // 取平均值 * 0.95，使其略低于平均价
        return avgPrice.multiply(new BigDecimal("0.95"));
    }

    /**
     * 基于成本计算价格
     */
    private BigDecimal calculateCostBasedPrice(BigDecimal costPrice) {
        // 默认目标利润率20%
        BigDecimal targetMargin = new BigDecimal("0.20");
        return costPrice.multiply(BigDecimal.ONE.add(targetMargin));
    }

    /**
     * 基于需求计算价格
     */
    private BigDecimal calculateDemandBasedPrice(Integer stockQuantity, Integer salesVelocity,
                                                   PriceHistoryService.PriceTrendAnalysis trend) {
        BigDecimal basePrice = BigDecimal.ZERO;

        // 如果有销售数据，基于销量计算需求
        if (salesVelocity != null && salesVelocity > 0) {
            // 销量高 = 需求旺，可以提价
            // 销量低 = 需求弱，需要降价
            BigDecimal demandFactor;
            
            if (salesVelocity > 100) {
                demandFactor = new BigDecimal("1.15"); // 热销提价15%
            } else if (salesVelocity > 50) {
                demandFactor = new BigDecimal("1.05"); // 较好提价5%
            } else if (salesVelocity > 20) {
                demandFactor = BigDecimal.ONE;         // 正常价格
            } else {
                demandFactor = new BigDecimal("0.90"); // 滞销降价10%
            }
            
            basePrice = basePrice.multiply(demandFactor);
        }

        // 如果有价格趋势数据
        if (trend != null && trend.getPriceChangePercent() != null) {
            // 价格上涨趋势：可以跟随提价
            // 价格下降趋势：需要降价保持竞争力
            basePrice = basePrice.add(trend.getPriceChangePercent().multiply(basePrice));
        }

        return basePrice;
    }

    /**
     * 确保价格有合理利润
     */
    private BigDecimal ensureProfitable(BigDecimal price, BigDecimal costPrice) {
        if (price.compareTo(costPrice) < 0) {
            // 价格低于成本，确保不低于成本价
            price = costPrice.multiply(BigDecimal.ONE.add(MIN_MARGIN));
            log.warn("价格低于成本，调整为: {}", price);
        }

        // 计算利润率
        BigDecimal margin = price.subtract(costPrice).divide(price, 4, RoundingMode.HALF_UP);
        
        if (margin.compareTo(MIN_MARGIN) < 0) {
            // 利润率过低，调整到最小利润率
            price = costPrice.multiply(BigDecimal.ONE.add(MIN_MARGIN));
            log.warn("利润率过低，调整为: {}", price);
        } else if (margin.compareTo(MAX_MARGIN) > 0) {
            // 利润率过高，可以降低价格增加竞争力
            price = costPrice.multiply(BigDecimal.ONE.add(MAX_MARGIN));
            log.info("利润率过高，降低价格: {}", price);
        }

        return price;
    }

    @Override
    public String getPricingStrategy(Long productId) {
        // 获取产品定价策略说明
        List<CompetitorProduct> competitors = competitorScrapeService.getCompetitorProducts(productId);
        
        if (competitors == null || competitors.isEmpty()) {
            return "新产品的初始定价策略";
        }

        BigDecimal avgCompetitorPrice = competitors.stream()
                .map(CompetitorProduct::getCurrentPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(competitors.size()), 2, RoundingMode.HALF_UP);

        return String.format("基于%d个竞品的动态定价，平均竞品价格: %.2f", 
                competitors.size(), avgCompetitorPrice);
    }

    @Override
    public void applyDynamicPricing(Long productId, BigDecimal costPrice, 
                                     Integer stockQuantity, Integer salesVelocity) {
        // 计算最优价格
        BigDecimal optimalPrice = calculateOptimalPrice(productId, costPrice, stockQuantity, salesVelocity);
        
        // 记录价格变更历史
        PriceHistory history = new PriceHistory();
        history.setProductId(productId);
        history.setOldPrice(optimalPrice); // 这里应该先查询当前价格
        history.setNewPrice(optimalPrice);
        history.setAdjustmentType("AUTO");
        history.setAdjustmentReason("基于竞品、成本、需求计算");
        history.setCreateTime(LocalDateTime.now());
        
        priceHistoryService.recordPriceChange(history);
        
        log.info("产品 {} 应用动态价格: {}", productId, optimalPrice);
    }
}