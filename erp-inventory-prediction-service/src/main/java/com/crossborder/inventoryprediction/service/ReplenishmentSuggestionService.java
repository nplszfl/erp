package com.crossborder.inventoryprediction.service;

import com.crossborder.inventoryprediction.dto.ReplenishmentSuggestion;

import java.util.List;

/**
 * 库存补货建议服务
 */
public interface ReplenishmentSuggestionService {

    /**
     * 生成补货建议
     * @param productId 产品ID
     * @return 补货建议列表
     */
    List<ReplenishmentSuggestion> generateSuggestions(Long productId);

    /**
     * 批量生成补货建议
     * @param productIds 产品ID列表
     * @return 补货建议列表
     */
    List<ReplenishmentSuggestion> batchGenerateSuggestions(List<Long> productIds);

    /**
     * 获取紧急补货建议（库存低于安全库存）
     * @return 紧急补货建议
     */
    List<ReplenishmentSuggestion> getUrgentReplenishments();

    /**
     * 计算最优补货量
     * @param productId 产品ID
     * @param targetDate 目标日期
     * @return 最优补货量
     */
    Integer calculateOptimalReplenishmentQuantity(Long productId, java.time.LocalDate targetDate);
}