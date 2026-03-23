package com.crossborder.pricing.service;

import java.math.BigDecimal;

/**
 * 动态定价服务接口
 */
public interface DynamicPricingService {

    /**
     * 计算产品最优价格
     * @param productId 产品ID
     * @param costPrice 成本价
     * @param stockQuantity 当前库存
     * @param salesVelocity 销售速度（单日销量）
     * @return 最优价格
     */
    BigDecimal calculateOptimalPrice(Long productId, BigDecimal costPrice, 
                                       Integer stockQuantity, Integer salesVelocity);

    /**
     * 获取定价策略说明
     */
    String getPricingStrategy(Long productId);

    /**
     * 应用动态定价
     */
    void applyDynamicPricing(Long productId, BigDecimal costPrice, 
                              Integer stockQuantity, Integer salesVelocity);
}