package com.crossborder.inventoryprediction.service.impl;

import com.crossborder.inventoryprediction.dto.PredictionRequest;
import com.crossborder.inventoryprediction.dto.PredictionResponse;
import com.crossborder.inventoryprediction.dto.ReplenishmentSuggestion;
import com.crossborder.inventoryprediction.service.InventoryPredictionService;
import com.crossborder.inventoryprediction.service.ReplenishmentSuggestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 补货建议服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReplenishmentSuggestionServiceImpl implements ReplenishmentSuggestionService {

    // 库存风险天数常量
    private static final int RISK_HIGH_DAYS = 7;
    private static final int RISK_MEDIUM_DAYS = 14;
    private static final int LEAD_TIME_DAYS = 3;

    private final InventoryPredictionService predictionService;

    @Override
    public List<ReplenishmentSuggestion> generateSuggestions(Long productId) {
        log.info("生成产品 {} 的补货建议", productId);

        // 获取预测结果
        PredictionResponse prediction = predictionService.predictSales(
            new PredictionRequest());

        // 假设数据（实际应从库存服务获取）
        int currentStock = 100;
        int safetyStock = 50;

        // 计算日均销量
        BigDecimal dailySales = prediction.getAvgDailySales() != null 
            ? BigDecimal.valueOf(prediction.getAvgDailySales()) 
            : BigDecimal.TEN;

        // 预测总销量
        BigDecimal predictedSales = prediction.getTotalPredictedSales() != null
            ? BigDecimal.valueOf(prediction.getTotalPredictedSales())
            : BigDecimal.valueOf(30).multiply(dailySales);

        // 计算缺货日期
        LocalDate stockoutDate = calculateStockoutDate(currentStock, dailySales);

        // 计算建议补货量
        int suggestedQty = calculateSuggestedQuantity(predictedSales, currentStock, safetyStock);

        // 评估缺货风险
        String risk = assessStockoutRisk(stockoutDate);

        // 计算优先级
        int priority = calculatePriority(risk, suggestedQty);

        // 生成建议
        ReplenishmentSuggestion suggestion = new ReplenishmentSuggestion();
        suggestion.setProductId(productId);
        suggestion.setCurrentStock(currentStock);
        suggestion.setSafetyStock(safetyStock);
        suggestion.setDailySales(dailySales);
        suggestion.setPredictedSales(predictedSales);
        suggestion.setSuggestedQuantity(suggestedQty);
        suggestion.setExpectedStockoutDate(stockoutDate);
        suggestion.setStockoutRisk(risk);
        suggestion.setPriority(priority);
        suggestion.setSuggestedReplenishmentDate(calculateReplenishmentDate(stockoutDate));
        suggestion.setReason(generateReason(risk, stockoutDate, suggestedQty));
        suggestion.setDaysOfStock(calculateDaysOfStock(currentStock, dailySales));

        List<ReplenishmentSuggestion> suggestions = new ArrayList<>();
        suggestions.add(suggestion);

        return suggestions;
    }

    @Override
    public List<ReplenishmentSuggestion> batchGenerateSuggestions(List<Long> productIds) {
        log.info("批量生成补货建议，产品数: {}", productIds.size());

        List<ReplenishmentSuggestion> allSuggestions = new ArrayList<>();

        for (Long productId : productIds) {
            try {
                List<ReplenishmentSuggestion> suggestions = generateSuggestions(productId);
                allSuggestions.addAll(suggestions);
            } catch (Exception e) {
                log.error("生成产品 {} 补货建议失败", productId, e);
            }
        }

        return allSuggestions.stream()
                .sorted(Comparator.comparingInt(ReplenishmentSuggestion::getPriority).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<ReplenishmentSuggestion> getUrgentReplenishments() {
        log.info("获取紧急补货建议");
        return new ArrayList<>();
    }

    @Override
    public Integer calculateOptimalReplenishmentQuantity(Long productId, LocalDate targetDate) {
        PredictionResponse prediction = predictionService.predictSales(
            new com.crossborder.inventoryprediction.dto.PredictionRequest());

        LocalDate today = LocalDate.now();
        int days = (int) ChronoUnit.DAYS.between(today, targetDate);

        BigDecimal dailySales = prediction.getAvgDailySales() != null 
            ? BigDecimal.valueOf(prediction.getAvgDailySales()) 
            : BigDecimal.TEN;

        int currentStock = 100;
        int safetyStock = 50;

        BigDecimal totalDemand = dailySales.multiply(BigDecimal.valueOf(days));
        int requiredStock = totalDemand.intValue() + safetyStock;
        int optimalQty = Math.max(0, requiredStock - currentStock);

        // 批量补货
        int caseSize = 100;
        optimalQty = ((optimalQty + caseSize - 1) / caseSize) * caseSize;

        return optimalQty;
    }

    private LocalDate calculateStockoutDate(int currentStock, BigDecimal dailySales) {
        if (dailySales.compareTo(BigDecimal.ZERO) <= 0) {
            return LocalDate.now().plusDays(999);
        }
        int daysUntilStockout = currentStock / dailySales.intValue();
        return LocalDate.now().plusDays(daysUntilStockout);
    }

    private int calculateSuggestedQuantity(BigDecimal predictedSales, int currentStock, int safetyStock) {
        int predictedQty = predictedSales.intValue();
        int requiredStock = predictedQty + safetyStock;
        int suggestedQty = requiredStock - currentStock;
        return Math.max(suggestedQty, safetyStock);
    }

    private String assessStockoutRisk(LocalDate stockoutDate) {
        if (stockoutDate == null) return "LOW";
        long days = ChronoUnit.DAYS.between(LocalDate.now(), stockoutDate);
        if (days <= RISK_HIGH_DAYS) return "HIGH";
        if (days <= RISK_MEDIUM_DAYS) return "MEDIUM";
        return "LOW";
    }

    private int calculatePriority(String risk, int suggestedQty) {
        int base = "HIGH".equals(risk) ? 5 : "MEDIUM".equals(risk) ? 3 : 1;
        if (suggestedQty > 1000) base += 1;
        return Math.min(5, base);
    }

    private LocalDate calculateReplenishmentDate(LocalDate stockoutDate) {
        return stockoutDate.minusDays(LEAD_TIME_DAYS);
    }

    private String generateReason(String risk, LocalDate stockoutDate, int suggestedQty) {
        String r = "HIGH".equals(risk) ? "库存不足，预计" + stockoutDate + "缺货" 
            : "MEDIUM".equals(risk) ? "库存偏低，建议补充" : "常规补货";
        return r + "，建议补货量: " + suggestedQty + "件";
    }

    private int calculateDaysOfStock(int currentStock, BigDecimal dailySales) {
        if (dailySales.compareTo(BigDecimal.ZERO) <= 0) return 999;
        return currentStock / dailySales.intValue();
    }
}