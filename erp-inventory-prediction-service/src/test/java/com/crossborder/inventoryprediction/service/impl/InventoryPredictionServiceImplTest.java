package com.crossborder.inventoryprediction.service.impl;

import com.crossborder.inventoryprediction.dto.PredictionRequest;
import com.crossborder.inventoryprediction.dto.PredictionResponse;
import com.crossborder.inventoryprediction.dto.ReplenishmentSuggestion;
import com.crossborder.inventoryprediction.service.InventoryPredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 库存预测服务单元测试
 * 
 * 测试销售预测、补货建议、库存预警等功能
 */
@SpringBootTest
@DisplayName("库存预测服务测试")
class InventoryPredictionServiceImplTest {

    @Autowired
    private InventoryPredictionService inventoryPredictionService;

    private PredictionRequest request;

    @BeforeEach
    void setUp() {
        request = new PredictionRequest();
        request.setProductId(1L);
        request.setProductCode("SKU001");
        request.setPredictionDays(30);
        request.setStartDate(LocalDate.now());
        request.setUseSeasonalModel(true);
        request.setUseTrendModel(true);
    }

    @Test
    @DisplayName("测试销售预测基本功能")
    void testPredictSales_Basic() {
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        assertNotNull(response);
        assertEquals("SKU001", response.getProductCode());
        assertEquals(30, response.getPredictionDays());
        assertNotNull(response.getDailyPredictions());
        assertEquals(30, response.getDailyPredictions().size());
        assertTrue(response.getTotalPredictedSales() > 0);
        assertTrue(response.getAvgDailySales() >= 0);
    }

    @Test
    @DisplayName("测试预测时间范围")
    void testPredictSales_DateRange() {
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        assertEquals(LocalDate.now(), response.getStartDate());
        assertEquals(LocalDate.now().plusDays(30), response.getEndDate());
    }

    @Test
    @DisplayName("测试模型类型")
    void testPredictSales_ModelType() {
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        assertNotNull(response.getModelType());
        assertTrue(response.getModelType().contains("Prophet") || 
                   response.getModelType().contains("ARIMA"));
    }

    @Test
    @DisplayName("测试每日预测数据完整性")
    void testPredictSales_DailyPredictions() {
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        for (PredictionResponse.DailyPrediction daily : response.getDailyPredictions()) {
            assertNotNull(daily.getDate());
            assertNotNull(daily.getPredictedSales());
            assertTrue(daily.getLowerBound() >= 0);
            assertTrue(daily.getUpperBound() >= daily.getLowerBound());
            assertTrue(daily.getConfidence() > 0 && daily.getConfidence() <= 1.0);
        }
    }

    @Test
    @DisplayName("测试趋势分析")
    void testPredictSales_TrendAnalysis() {
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        assertNotNull(response.getTrend());
        assertNotNull(response.getTrend().getDirection());
        assertTrue(response.getTrend().getGrowthRate() != null);
    }

    @Test
    @DisplayName("测试季节性分析")
    void testPredictSales_SeasonalAnalysis() {
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        assertNotNull(response.getSeasonalFactors());
        assertTrue(response.getSeasonalFactors().size() > 0);
    }

    @Test
    @DisplayName("测试峰值和谷值")
    void testPredictSales_PeakAndTrough() {
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        assertNotNull(response.getPeakSales());
        assertNotNull(response.getPeakDate());
        assertNotNull(response.getTroughSales());
        assertNotNull(response.getTroughDate());
        assertTrue(response.getPeakSales() >= response.getTroughSales());
    }

    @Test
    @DisplayName("测试置信区间")
    void testPredictSales_ConfidenceInterval() {
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        assertNotNull(response.getConfidenceInterval());
        assertTrue(response.getConfidenceInterval().getLower() >= 0);
        assertTrue(response.getConfidenceInterval().getUpper() >= 
                   response.getConfidenceInterval().getLower());
    }

    @Test
    @DisplayName("测试短期预测（7天）")
    void testPredictSales_ShortTerm() {
        request.setPredictionDays(7);
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        assertEquals(7, response.getDailyPredictions().size());
        assertEquals(7, response.getPredictionDays());
    }

    @Test
    @DisplayName("测试长期预测（90天）")
    void testPredictSales_LongTerm() {
        request.setPredictionDays(90);
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        assertEquals(90, response.getDailyPredictions().size());
        assertEquals(90, response.getPredictionDays());
    }

    @Test
    @DisplayName("测试不使用季节性模型")
    void testPredictSales_WithoutSeasonalModel() {
        request.setUseSeasonalModel(false);
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        assertNotNull(response);
        assertNotNull(response.getDailyPredictions());
    }

    @Test
    @DisplayName("测试不使用趋势模型")
    void testPredictSales_WithoutTrendModel() {
        request.setUseTrendModel(false);
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        assertNotNull(response);
        assertNotNull(response.getDailyPredictions());
    }

    @Test
    @DisplayName("测试补货建议生成")
    void testGetReplenishmentSuggestions() {
        List<ReplenishmentSuggestion> suggestions = 
                inventoryPredictionService.getReplenishmentSuggestions(1L, 30);

        assertNotNull(suggestions);
        // 验证补货建议结构
        for (ReplenishmentSuggestion suggestion : suggestions) {
            assertNotNull(suggestion.getProductId());
            assertNotNull(suggestion.getSuggestedQuantity());
            assertNotNull(suggestion.getUrgency());
            assertNotNull(suggestion.getEstimatedArrivalDate());
        }
    }

    @Test
    @DisplayName("测试多产品补货建议")
    void testGetReplenishmentSuggestions_MultipleProducts() {
        List<Long> productIds = List.of(1L, 2L, 3L);
        List<ReplenishmentSuggestion> suggestions = 
                inventoryPredictionService.getReplenishmentSuggestions(productIds);

        assertNotNull(suggestions);
        assertTrue(suggestions.size() <= productIds.size() * 30); // 每个产品最多30天建议
    }

    @Test
    @DisplayName("测试补货建议紧急程度")
    void testReplenishmentSuggestion_Urgency() {
        List<ReplenishmentSuggestion> suggestions = 
                inventoryPredictionService.getReplenishmentSuggestions(1L, 30);

        for (ReplenishmentSuggestion suggestion : suggestions) {
            assertTrue(suggestion.getUrgency() == ReplenishmentSuggestion.Urgency.HIGH ||
                       suggestion.getUrgency() == ReplenishmentSuggestion.Urgency.MEDIUM ||
                       suggestion.getUrgency() == ReplenishmentSuggestion.Urgency.LOW);
        }
    }

    @Test
    @DisplayName("测试库存预警")
    void testGetInventoryAlerts() {
        List<PredictionResponse.InventoryAlert> alerts = 
                inventoryPredictionService.getInventoryAlerts(1L);

        assertNotNull(alerts);
        for (PredictionResponse.InventoryAlert alert : alerts) {
            assertNotNull(alert.getAlertType());
            assertNotNull(alert.getLevel());
            assertNotNull(alert.getMessage());
        }
    }

    @Test
    @DisplayName("测试库存预警级别")
    void testInventoryAlert_Levels() {
        List<PredictionResponse.InventoryAlert> alerts = 
                inventoryPredictionService.getInventoryAlerts(1L);

        for (PredictionResponse.InventoryAlert alert : alerts) {
            assertTrue(alert.getLevel() == PredictionResponse.AlertLevel.CRITICAL ||
                       alert.getLevel() == PredictionResponse.AlertLevel.WARNING ||
                       alert.getLevel() == PredictionResponse.AlertLevel.INFO);
        }
    }

    @Test
    @DisplayName("测试批量库存预警")
    void testGetInventoryAlerts_MultipleProducts() {
        List<Long> productIds = List.of(1L, 2L, 3L);
        List<PredictionResponse.InventoryAlert> alerts = 
                inventoryPredictionService.getInventoryAlerts(productIds);

        assertNotNull(alerts);
    }

    @Test
    @DisplayName("测试库存优化建议")
    void testGetInventoryOptimization() {
        PredictionResponse.InventoryOptimization optimization = 
                inventoryPredictionService.getInventoryOptimization(1L);

        assertNotNull(optimization);
        assertNotNull(optimization.getCurrentStock());
        assertNotNull(optimization.getOptimalStock());
        assertTrue(optimization.getTurnoverRate() >= 0);
    }

    @Test
    @DisplayName("测试响应时间记录")
    void testResponseTime() {
        long startTime = System.currentTimeMillis();
        PredictionResponse response = inventoryPredictionService.predictSales(request);
        long endTime = System.currentTimeMillis();

        assertTrue(endTime - startTime < 5000); // 应该在5秒内完成
        assertNotNull(response.getPredictionTime());
    }

    @Test
    @DisplayName("测试空产品ID处理")
    void testPredictSales_NullProductId() {
        request.setProductId(null);
        request.setProductCode("TEST001");
        
        PredictionResponse response = inventoryPredictionService.predictSales(request);
        
        assertNotNull(response);
        assertEquals("TEST001", response.getProductCode());
    }

    @Test
    @DisplayName("测试边界值：预测天数为1")
    void testPredictSales_OneDay() {
        request.setPredictionDays(1);
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        assertEquals(1, response.getDailyPredictions().size());
    }

    @Test
    @DisplayName("测试边界值：预测天数为0")
    void testPredictSales_ZeroDays() {
        request.setPredictionDays(0);
        PredictionResponse response = inventoryPredictionService.predictSales(request);

        assertNotNull(response.getDailyPredictions());
    }
}