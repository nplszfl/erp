package com.crossborder.pricing.service.impl;

import com.crossborder.pricing.dto.PricingRequest;
import com.crossborder.pricing.dto.PricingResponse;
import com.crossborder.pricing.service.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 智能定价服务测试
 */
@SpringBootTest
class PricingServiceImplTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new PricingServiceImpl();
    }

    @Test
    @DisplayName("测试计算最优价格 - 基本场景")
    void testCalculateOptimalPrice_BasicScenario() {
        // 准备测试数据
        PricingRequest request = new PricingRequest();
        request.setProductId(1L);
        request.setProductCode("TEST-001");
        request.setCostPrice(new BigDecimal("100.00"));
        request.setCurrentPrice(new BigDecimal("150.00"));
        request.setTargetProfitMargin(new BigDecimal("25.00"));
        request.setEnableCompetitorAnalysis(false);
        request.setEnableSeasonalAdjustment(false);
        request.setEnableInventoryAdjustment(false);

        // 执行测试
        PricingResponse response = pricingService.calculateOptimalPrice(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getProductId());
        assertEquals("TEST-001", response.getProductCode());
        assertNotNull(response.getRecommendedPrice());
        assertNotNull(response.getProfitMargin());
        assertTrue(response.getRecommendedPrice().compareTo(request.getCostPrice()) > 0);
    }

    @Test
    @DisplayName("测试计算最优价格 - 启用竞品分析")
    void testCalculateOptimalPrice_WithCompetitorAnalysis() {
        // 准备测试数据
        PricingRequest request = new PricingRequest();
        request.setProductId(2L);
        request.setProductCode("TEST-002");
        request.setCostPrice(new BigDecimal("80.00"));
        request.setCurrentPrice(new BigDecimal("120.00"));
        request.setTargetProfitMargin(new BigDecimal("20.00"));
        request.setEnableCompetitorAnalysis(true);
        request.setEnableSeasonalAdjustment(false);
        request.setEnableInventoryAdjustment(false);

        // 执行测试
        PricingResponse response = pricingService.calculateOptimalPrice(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getCompetitorAvgPrice());
        assertNotNull(response.getCompetitorMinPrice());
        assertNotNull(response.getCompetitorMaxPrice());
        assertTrue(response.getCompetitorMinPrice().compareTo(response.getCompetitorAvgPrice()) <= 0);
        assertTrue(response.getCompetitorMaxPrice().compareTo(response.getCompetitorAvgPrice()) >= 0);
    }

    @Test
    @DisplayName("测试计算最优价格 - 启用所有调整因子")
    void testCalculateOptimalPrice_WithAllFactors() {
        // 准备测试数据
        PricingRequest request = new PricingRequest();
        request.setProductId(3L);
        request.setProductCode("TEST-003");
        request.setCostPrice(new BigDecimal("150.00"));
        request.setCurrentPrice(new BigDecimal("200.00"));
        request.setTargetProfitMargin(new BigDecimal("30.00"));
        request.setEnableCompetitorAnalysis(true);
        request.setEnableSeasonalAdjustment(true);
        request.setEnableInventoryAdjustment(true);

        // 执行测试
        PricingResponse response = pricingService.calculateOptimalPrice(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getRecommendedPrice());
        assertNotNull(response.getFactorContributions());
        assertTrue(response.getProfitMargin().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("测试成本价小于等于0时抛出异常")
    void testCalculateOptimalPrice_ZeroCostPrice() {
        // 准备测试数据
        PricingRequest request = new PricingRequest();
        request.setProductId(4L);
        request.setProductCode("TEST-004");
        request.setCostPrice(BigDecimal.ZERO);
        request.setCurrentPrice(new BigDecimal("100.00"));
        request.setTargetProfitMargin(new BigDecimal("20.00"));

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> {
            pricingService.calculateOptimalPrice(request);
        });
    }

    @Test
    @DisplayName("测试批量计算最优价格")
    void testBatchCalculateOptimalPrice() {
        // 准备测试数据
        PricingRequest request1 = new PricingRequest();
        request1.setProductId(1L);
        request1.setProductCode("BATCH-001");
        request1.setCostPrice(new BigDecimal("50.00"));
        request1.setCurrentPrice(new BigDecimal("75.00"));
        request1.setTargetProfitMargin(new BigDecimal("25.00"));

        PricingRequest request2 = new PricingRequest();
        request2.setProductId(2L);
        request2.setProductCode("BATCH-002");
        request2.setCostPrice(new BigDecimal("100.00"));
        request2.setCurrentPrice(new BigDecimal("150.00"));
        request2.setTargetProfitMargin(new BigDecimal("30.00"));

        List<PricingRequest> requests = List.of(request1, request2);

        // 执行测试
        List<PricingResponse> responses = pricingService.batchCalculateOptimalPrice(requests);

        // 验证结果
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertNotNull(responses.get(0).getRecommendedPrice());
        assertNotNull(responses.get(1).getRecommendedPrice());
    }

    @Test
    @DisplayName("测试根据竞品调整价格")
    void testAdjustPriceByCompetitors() {
        Long productId = 5L;

        // 执行测试
        PricingResponse response = pricingService.adjustPriceByCompetitors(productId);

        // 验证结果
        assertNotNull(response);
        assertEquals(productId, response.getProductId());
        assertNotNull(response.getRecommendedPrice());
        assertTrue(response.getApplied());
    }

    @Test
    @DisplayName("测试手动调整价格")
    void testManualPriceAdjustment() {
        Long productId = 6L;
        BigDecimal targetPrice = new BigDecimal("199.99");
        String reason = "促销活动调整";

        // 执行测试
        PricingResponse response = pricingService.manualPriceAdjustment(productId, targetPrice, reason);

        // 验证结果
        assertNotNull(response);
        assertEquals(productId, response.getProductId());
        assertEquals(targetPrice, response.getRecommendedPrice());
        assertTrue(response.getAdjustmentReason().contains(reason));
        assertTrue(response.getApplied());
    }

    @Test
    @DisplayName("测试获取产品定价信息")
    void testGetProductPricingInfo() {
        Long productId = 7L;

        // 执行测试
        PricingResponse response = pricingService.getProductPricingInfo(productId);

        // 验证结果
        assertNotNull(response);
        assertEquals(productId, response.getProductId());
        assertNotNull(response.getProductCode());
        assertNotNull(response.getOriginalPrice());
        assertNotNull(response.getRecommendedPrice());
        assertNotNull(response.getProfitMargin());
        assertNotNull(response.getPricingStrategy());
    }

    @Test
    @DisplayName("测试高利润率场景")
    void testCalculateOptimalPrice_HighProfitMargin() {
        // 准备测试数据 - 50%利润率
        PricingRequest request = new PricingRequest();
        request.setProductId(8L);
        request.setProductCode("HIGH-MARGIN-001");
        request.setCostPrice(new BigDecimal("100.00"));
        request.setCurrentPrice(new BigDecimal("200.00"));
        request.setTargetProfitMargin(new BigDecimal("50.00"));
        request.setEnableCompetitorAnalysis(false);
        request.setEnableSeasonalAdjustment(false);
        request.setEnableInventoryAdjustment(false);

        // 执行测试
        PricingResponse response = pricingService.calculateOptimalPrice(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getRecommendedPrice());
        // 预期利润率应该接近50%
        assertTrue(response.getProfitMargin().compareTo(new BigDecimal("45")) >= 0);
    }

    @Test
    @DisplayName("测试低利润率场景")
    void testCalculateOptimalPrice_LowProfitMargin() {
        // 准备测试数据 - 10%利润率
        PricingRequest request = new PricingRequest();
        request.setProductId(9L);
        request.setProductCode("LOW-MARGIN-001");
        request.setCostPrice(new BigDecimal("100.00"));
        request.setCurrentPrice(new BigDecimal("110.00"));
        request.setTargetProfitMargin(new BigDecimal("10.00"));
        request.setEnableCompetitorAnalysis(false);
        request.setEnableSeasonalAdjustment(false);
        request.setEnableInventoryAdjustment(false);

        // 执行测试
        PricingResponse response = pricingService.calculateOptimalPrice(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getRecommendedPrice());
        // 预期利润率应该接近10%
        assertTrue(response.getProfitMargin().compareTo(new BigDecimal("15")) <= 0);
    }

    @Test
    @DisplayName("测试价格变动计算")
    void testPriceChangeCalculation() {
        // 准准备测试数据 - 当前价格高于推荐价格
        PricingRequest request = new PricingRequest();
        request.setProductId(10L);
        request.setProductCode("PRICE-DOWN-001");
        request.setCostPrice(new BigDecimal("100.00"));
        request.setCurrentPrice(new BigDecimal("200.00"));
        request.setTargetProfitMargin(new BigDecimal("20.00"));
        request.setEnableCompetitorAnalysis(false);
        request.setEnableSeasonalAdjustment(false);
        request.setEnableInventoryAdjustment(false);

        // 执行测试
        PricingResponse response = pricingService.calculateOptimalPrice(request);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getPriceChangePercent());
        // 由于当前价格(200)远高于成本价(100)的20%利润率价格(120)，价格应该下降
        assertTrue(response.getPriceChangePercent().compareTo(BigDecimal.ZERO) < 0);
    }
}
