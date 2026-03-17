package com.crossborder.pricing.service;

import com.crossborder.pricing.dto.PricingRequest;
import com.crossborder.pricing.dto.PricingResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 智能定价服务
 *
 * 核心算法：
 * 1. 竞品分析 - 抓取竞品价格，计算市场价格区间
 * 2. 成本加成 - 基于成本和目标利润率计算基础价格
 * 3. 动态调整 - 根据季节性、库存、需求等因素调整价格
 * 4. 边界检查 - 确保价格在合理范围内
 */
public interface PricingService {

    /**
     * 计算最优价格
     */
    PricingResponse calculateOptimalPrice(PricingRequest request);

    /**
     * 批量计算最优价格
     */
    List<PricingResponse> batchCalculateOptimalPrice(List<PricingRequest> requests);

    /**
     * 根据竞品数据调整价格
     */
    PricingResponse adjustPriceByCompetitors(Long productId);

    /**
     * 手动触发价格调整
     */
    PricingResponse manualPriceAdjustment(Long productId, BigDecimal targetPrice, String reason);

    /**
     * 获取产品当前定价信息
     */
    PricingResponse getProductPricingInfo(Long productId);
}
