package com.crossborder.pricing.service.impl;

import com.crossborder.pricing.dto.PricingRequest;
import com.crossborder.pricing.dto.PricingResponse;
import com.crossborder.pricing.service.PricingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能定价服务实现
 *
 * 核心算法：
 * 1. 竞品分析 - 获取市场价格区间
 * 2. 成本加成 - 基于成本和利润率计算
 * 3. 动态调整 - 季节性、库存、需求因子
 * 4. 边界检查 - 确保价格合理
 */
@Slf4j
@Service
public class PricingServiceImpl implements PricingService {

    // 配置参数
    private static final BigDecimal BASE_PROFIT_MARGIN = new BigDecimal("0.20"); // 20%基础利润率
    private static final BigDecimal MAX_PRICE_INCREASE = new BigDecimal("0.15"); // 最多涨15%
    private static final BigDecimal MAX_PRICE_DECREASE = new BigDecimal("0.10"); // 最多降10%

    @Override
    public PricingResponse calculateOptimalPrice(PricingRequest request) {
        log.info("开始计算最优价格 - 产品ID: {}, 产品编码: {}", request.getProductId(), request.getProductCode());

        PricingResponse response = new PricingResponse();
        response.setProductId(request.getProductId());
        response.setProductCode(request.getProductCode());
        response.setOriginalPrice(request.getCurrentPrice());
        response.setCalculationTime(LocalDateTime.now());

        // 1. 基于成本和利润率计算基础价格
        BigDecimal basePrice = calculateBasePrice(request.getCostPrice(), request.getTargetProfitMargin());
        log.info("基础价格（成本+利润）: {}", basePrice);

        // 2. 竞品分析
        if (Boolean.TRUE.equals(request.getEnableCompetitorAnalysis())) {
            Map<String, BigDecimal> competitorInfo = analyzeCompetitors(request.getProductId());
            response.setCompetitorAvgPrice(competitorInfo.get("avg"));
            response.setCompetitorMinPrice(competitorInfo.get("min"));
            response.setCompetitorMaxPrice(competitorInfo.get("max"));

            // 基于竞品调整价格
            BigDecimal marketAdjustedPrice = adjustPriceByMarket(basePrice, competitorInfo);
            log.info("市场调整后价格: {}", marketAdjustedPrice);
            basePrice = marketAdjustedPrice;
        }

        // 3. 季节性调整
        if (Boolean.TRUE.equals(request.getEnableSeasonalAdjustment())) {
            BigDecimal seasonalFactor = getSeasonalFactor();
            BigDecimal seasonalAdjustedPrice = basePrice.multiply(BigDecimal.ONE.add(seasonalFactor));
            log.info("季节性因子: {}, 调整后价格: {}", seasonalFactor, seasonalAdjustedPrice);
            basePrice = seasonalAdjustedPrice;
        }

        // 4. 库存调整
        if (Boolean.TRUE.equals(request.getEnableInventoryAdjustment())) {
            BigDecimal inventoryFactor = getInventoryFactor(request.getProductId());
            BigDecimal inventoryAdjustedPrice = basePrice.multiply(BigDecimal.ONE.add(inventoryFactor));
            log.info("库存因子: {}, 调整后价格: {}", inventoryFactor, inventoryAdjustedPrice);
            basePrice = inventoryAdjustedPrice;
        }

        // 5. 边界检查
        BigDecimal finalPrice = validatePrice(basePrice, request.getCostPrice());

        // 6. 计算变动
        BigDecimal priceChangePercent = calculatePriceChange(request.getCurrentPrice(), finalPrice);
        BigDecimal profitMargin = calculateProfitMargin(finalPrice, request.getCostPrice());

        // 7. 设置响应
        response.setRecommendedPrice(finalPrice);
        response.setPriceChangePercent(priceChangePercent);
        response.setProfitMargin(profitMargin);
        response.setPricingStrategy("AI智能定价算法 v1.5.0");
        response.setAdjustmentReason(generateAdjustmentReason(priceChangePercent));

        // 8. 保存因子贡献
        Map<String, BigDecimal> contributions = new HashMap<>();
        contributions.put("basePrice", request.getCostPrice());
        contributions.put("profitMargin", request.getTargetProfitMargin());
        if (response.getCompetitorAvgPrice() != null) {
            contributions.put("competitorFactor", response.getCompetitorAvgPrice());
        }
        response.setFactorContributions(contributions);

        log.info("最优价格计算完成 - 推荐价格: {}, 预期利润率: {}%", finalPrice, profitMargin.multiply(new BigDecimal("100")));
        return response;
    }

    @Override
    public List<PricingResponse> batchCalculateOptimalPrice(List<PricingRequest> requests) {
        log.info("批量计算最优价格 - 数量: {}", requests.size());
        return requests.stream()
                .map(this::calculateOptimalPrice)
                .toList();
    }

    @Override
    public PricingResponse adjustPriceByCompetitors(Long productId) {
        log.info("根据竞品调整价格 - 产品ID: {}", productId);

        // 模拟：从数据库获取产品信息
        PricingRequest request = new PricingRequest();
        request.setProductId(productId);
        request.setProductCode("PROD-" + productId);
        request.setCostPrice(new BigDecimal("100.00"));
        request.setCurrentPrice(new BigDecimal("150.00"));
        request.setTargetProfitMargin(BASE_PROFIT_MARGIN.multiply(new BigDecimal("100")));
        request.setEnableCompetitorAnalysis(true);
        request.setEnableSeasonalAdjustment(true);
        request.setEnableInventoryAdjustment(true);

        PricingResponse response = calculateOptimalPrice(request);
        response.setApplied(true);

        // TODO: 实际更新数据库中的价格

        log.info("价格调整完成 - 产品ID: {}, 新价格: {}", productId, response.getRecommendedPrice());
        return response;
    }

    @Override
    public PricingResponse manualPriceAdjustment(Long productId, BigDecimal targetPrice, String reason) {
        log.info("手动调整价格 - 产品ID: {}, 目标价格: {}, 原因: {}", productId, targetPrice, reason);

        PricingResponse response = new PricingResponse();
        response.setProductId(productId);
        response.setRecommendedPrice(targetPrice);
        response.setAdjustmentReason("手动调整: " + reason);
        response.setApplied(true);
        response.setCalculationTime(LocalDateTime.now());

        // TODO: 实际更新数据库中的价格

        log.info("手动价格调整完成");
        return response;
    }

    @Override
    public PricingResponse getProductPricingInfo(Long productId) {
        log.info("获取产品定价信息 - 产品ID: {}", productId);

        // 模拟：从数据库获取定价信息
        PricingResponse response = new PricingResponse();
        response.setProductId(productId);
        response.setProductCode("PROD-" + productId);
        response.setOriginalPrice(new BigDecimal("150.00"));
        response.setRecommendedPrice(new BigDecimal("145.00"));
        response.setProfitMargin(new BigDecimal("30"));
        response.setPricingStrategy("AI智能定价算法 v1.5.0");
        response.setCalculationTime(LocalDateTime.now());

        return response;
    }

    // ========== 私有方法 ==========

    /**
     * 基于成本和利润率计算基础价格
     */
    private BigDecimal calculateBasePrice(BigDecimal costPrice, BigDecimal targetProfitMargin) {
        if (costPrice == null || costPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("成本价格必须大于0");
        }

        BigDecimal profitMargin = targetProfitMargin != null
                ? targetProfitMargin.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
                : BASE_PROFIT_MARGIN;

        return costPrice.multiply(BigDecimal.ONE.add(profitMargin))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 竞品分析（模拟）
     */
    private Map<String, BigDecimal> analyzeCompetitors(Long productId) {
        // 模拟竞品数据
        // 实际应该从竞品数据库获取
        BigDecimal minPrice = new BigDecimal("130.00");
        BigDecimal maxPrice = new BigDecimal("180.00");
        BigDecimal avgPrice = new BigDecimal("150.00");

        Map<String, BigDecimal> info = new HashMap<>();
        info.put("min", minPrice);
        info.put("max", maxPrice);
        info.put("avg", avgPrice);

        return info;
    }

    /**
     * 基于市场价格调整
     */
    private BigDecimal adjustPriceByMarket(BigDecimal currentPrice, Map<String, BigDecimal> competitorInfo) {
        BigDecimal marketAvg = competitorInfo.get("avg");

        // 如果当前价格低于市场均价，适当提高
        if (currentPrice.compareTo(marketAvg) < 0) {
            BigDecimal gap = marketAvg.subtract(currentPrice);
            BigDecimal adjustment = gap.multiply(new BigDecimal("0.5")); // 调整50%的差距
            return currentPrice.add(adjustment);
        }

        // 如果当前价格高于市场均价，适当降低
        if (currentPrice.compareTo(marketAvg) > 0) {
            BigDecimal gap = currentPrice.subtract(marketAvg);
            BigDecimal adjustment = gap.multiply(new BigDecimal("0.3")); // 调整30%的差距
            return currentPrice.subtract(adjustment);
        }

        return currentPrice;
    }

    /**
     * 获取季节性因子（模拟）
     */
    private BigDecimal getSeasonalFactor() {
        // 实际应该根据历史数据和当前日期计算
        // 3-5月春季旺季，+2%；11-12月购物季，+5%
        return new BigDecimal("0.02");
    }

    /**
     * 获取库存因子（模拟）
     */
    private BigDecimal getInventoryFactor(Long productId) {
        // 模拟：库存低时涨价，库存高时降价
        // 实际应该从库存服务获取
        return BigDecimal.ZERO;
    }

    /**
     * 验证价格边界
     */
    private BigDecimal validatePrice(BigDecimal price, BigDecimal costPrice) {
        // 确保价格不低于成本
        if (price.compareTo(costPrice) < 0) {
            price = costPrice.multiply(new BigDecimal("1.05")); // 至少5%利润
        }

        // 确保价格不超过成本+50%
        BigDecimal maxPrice = costPrice.multiply(new BigDecimal("1.50"));
        if (price.compareTo(maxPrice) > 0) {
            price = maxPrice;
        }

        return price.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算价格变动百分比
     */
    private BigDecimal calculatePriceChange(BigDecimal originalPrice, BigDecimal newPrice) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return newPrice.subtract(originalPrice)
                .divide(originalPrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算利润率
     */
    private BigDecimal calculateProfitMargin(BigDecimal sellingPrice, BigDecimal costPrice) {
        if (costPrice == null || costPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return sellingPrice.subtract(costPrice)
                .divide(sellingPrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 生成调整原因
     */
    private String generateAdjustmentReason(BigDecimal priceChangePercent) {
        if (priceChangePercent.compareTo(BigDecimal.ZERO) == 0) {
            return "价格无需调整";
        }

        if (priceChangePercent.compareTo(BigDecimal.ZERO) > 0) {
            return String.format("价格上涨%.2f%%，基于竞品分析和市场需求", priceChangePercent);
        }

        return String.format("价格下降%.2f%%，基于市场竞争力分析", priceChangePercent.abs());
    }
}
