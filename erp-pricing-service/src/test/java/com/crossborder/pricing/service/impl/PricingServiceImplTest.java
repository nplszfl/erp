package com.crossborder.pricing.service.impl;

import com.crossborder.pricing.dto.*;
import com.crossborder.pricing.service.DynamicPricingService;
import com.crossborder.pricing.service.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 定价服务测试
 */
@ExtendWith(MockitoExtension.class)
class PricingServiceImplTest {

    @Mock
    private DynamicPricingService dynamicPricingService;

    @InjectMocks
    private PricingServiceImpl pricingService;

    private PricingRequest pricingRequest;
    private ProductCostInfo costInfo;
    private CompetitorPriceInfo competitorInfo;

    @BeforeEach
    void setUp() {
        // 初始化定价请求
        pricingRequest = new PricingRequest();
        pricingRequest.setProductId(1001L);
        pricingRequest.setProductCode("SKU-001");
        pricingRequest.setCostPrice(new BigDecimal("50.00"));
        pricingRequest.setTargetProfitMargin(0.2);
        pricingRequest.setPlatform("AMAZON");
        pricingRequest.setCompetitorCount(5);

        // 初始化成本信息
        costInfo = new ProductCostInfo();
        costInfo.setProductId(1001L);
        costInfo.setMaterialCost(new BigDecimal("30.00"));
        costInfo.setLaborCost(new BigDecimal("10.00"));
        costInfo.setOverheadCost(new BigDecimal("5.00"));
        costInfo.setShippingCost(new BigDecimal("5.00"));
        costInfo.setTotalCost(new BigDecimal("50.00"));

        // 初始化竞品价格信息
        competitorInfo = new CompetitorPriceInfo();
        competitorInfo.setProductId(1001L);
        competitorInfo.setLowestPrice(new BigDecimal("65.00"));
        competitorInfo.setHighestPrice(new BigDecimal("89.00"));
        competitorInfo.setAveragePrice(new BigDecimal("75.00"));
        competitorInfo.setMedianPrice(new BigDecimal("73.00"));
    }

    @Test
    void testCalculateBasePrice() {
        // 测试基础价格计算
        BigDecimal costPrice = new BigDecimal("50.00");
        double profitMargin = 0.2;

        BigDecimal basePrice = pricingService.calculateBasePrice(costPrice, profitMargin);

        // 期望: 50 / (1 - 0.2) = 62.50
        assertEquals(new BigDecimal("62.50"), basePrice);
    }

    @Test
    void testCalculateBasePriceWithZeroMargin() {
        // 测试零利润率的定价
        BigDecimal costPrice = new BigDecimal("50.00");
        double profitMargin = 0.0;

        BigDecimal basePrice = pricingService.calculateBasePrice(costPrice, profitMargin);

        assertEquals(new BigDecimal("50.00"), basePrice);
    }

    @Test
    void testCalculateBasePriceWithHighMargin() {
        // 测试高利润率的定价
        BigDecimal costPrice = new BigDecimal("50.00");
        double profitMargin = 0.5;

        BigDecimal basePrice = pricingService.calculateBasePrice(costPrice, profitMargin);

        // 期望: 50 / (1 - 0.5) = 100.00
        assertEquals(new BigDecimal("100.00"), basePrice);
    }

    @Test
    void testApplyPlatformFee() {
        // 测试平台费用应用
        BigDecimal basePrice = new BigDecimal("62.50");
        String platform = "AMAZON";

        BigDecimal finalPrice = pricingService.applyPlatformFee(basePrice, platform);

        // Amazon平台费率约15%
        assertTrue(finalPrice.compareTo(basePrice) > 0);
    }

    @Test
    void testApplyPlatformFeeEbay() {
        // 测试eBay平台费用
        BigDecimal basePrice = new BigDecimal("62.50");
        String platform = "EBAY";

        BigDecimal finalPrice = pricingService.applyPlatformFee(basePrice, platform);

        // eBay平台费率约12.9%
        assertTrue(finalPrice.compareTo(basePrice) > 0);
    }

    @Test
    void testCalculateProfit() {
        // 测试利润计算
        BigDecimal sellingPrice = new BigDecimal("80.00");
        BigDecimal costPrice = new BigDecimal("50.00");

        BigDecimal profit = pricingService.calculateProfit(sellingPrice, costPrice);

        assertEquals(new BigDecimal("30.00"), profit);
    }

    @Test
    void testCalculateProfitMargin() {
        // 测试利润率计算
        BigDecimal sellingPrice = new BigDecimal("80.00");
        BigDecimal costPrice = new BigDecimal("50.00");

        double profitMargin = pricingService.calculateProfitMargin(sellingPrice, costPrice);

        // 利润率: (80-50)/80 = 0.375 = 37.5%
        assertEquals(0.375, profitMargin, 0.001);
    }

    @Test
    void testIsPriceCompetitive() {
        // 测试价格竞争力
        BigDecimal myPrice = new BigDecimal("70.00");
        
        when(dynamicPricingService.getCompetitorPrices(anyLong()))
            .thenReturn(Arrays.asList(
                createCompetitorPrice("A", new BigDecimal("75.00")),
                createCompetitorPrice("B", new BigDecimal("72.00")),
                createCompetitorPrice("C", new BigDecimal("68.00"))
            ));

        boolean isCompetitive = pricingService.isPriceCompetitive(1001L, myPrice);

        assertTrue(isCompetitive); // 70在68-75之间，属于有竞争力
    }

    @Test
    void testIsPriceNotCompetitive() {
        // 测试价格缺乏竞争力
        BigDecimal myPrice = new BigDecimal("95.00"));
        
        when(dynamicPricingService.getCompetitorPrices(anyLong()))
            .thenReturn(Arrays.asList(
                createCompetitorPrice("A", new BigDecimal("75.00")),
                createCompetitorPrice("B", new BigDecimal("72.00")),
                createCompetitorPrice("C", new BigDecimal("68.00"))
            ));

        boolean isCompetitive = pricingService.isPriceCompetitive(1001L, myPrice);

        assertFalse(isCompetitive); // 95高于最高价，缺乏竞争力
    }

    @Test
    void testCalculateRecommendedPrice() {
        // 测试推荐价格计算
        when(dynamicPricingService.getCompetitorPrices(anyLong()))
            .thenReturn(Arrays.asList(
                createCompetitorPrice("A", new BigDecimal("65.00")),
                createCompetitorPrice("B", new BigDecimal("70.00")),
                createCompetitorPrice("C", new BigDecimal("75.00"))
            ));

        BigDecimal costPrice = new BigDecimal("50.00");
        double targetMargin = 0.2;

        BigDecimal recommendedPrice = pricingService.calculateRecommendedPrice(
            1001L, costPrice, targetMargin);

        // 推荐价格应该在成本和最低竞品之间，确保竞争力
        assertNotNull(recommendedPrice);
        assertTrue(recommendedPrice.compareTo(costPrice) > 0);
    }

    @Test
    void testApplyDynamicPricingStrategy() {
        // 测试动态定价策略应用
        BigDecimal basePrice = new BigDecimal("70.00");
        
        // 模拟低库存情况
        when(dynamicPricingService.getInventoryLevel(anyLong()))
            .thenReturn(5); // 低库存
        
        when(dynamicPricingService.getDemandLevel(anyLong()))
            .thenReturn(8); // 高需求

        BigDecimal adjustedPrice = pricingService.applyDynamicPricingStrategy(
            1001L, basePrice, "LOW_STOCK_HIGH_DEMAND");

        // 低库存高需求应该提价
        assertTrue(adjustedPrice.compareTo(basePrice) > 0);
    }

    @Test
    void testApplyDiscountStrategy() {
        // 测试折扣策略应用
        BigDecimal basePrice = new BigDecimal("70.00");
        
        when(dynamicPricingService.getInventoryLevel(anyLong()))
            .thenReturn(100); // 高库存

        BigDecimal discountedPrice = pricingService.applyDynamicPricingStrategy(
            1001L, basePrice, "HIGH_STOCK_LOW_DEMAND");

        // 高库存应该降价促销
        assertTrue(discountedPrice.compareTo(basePrice) < 0);
    }

    @Test
    void testCalculateVolumeDiscount() {
        // 测试批量折扣计算
        BigDecimal unitPrice = new BigDecimal("10.00");
        int quantity = 100;

        BigDecimal discountedPrice = pricingService.calculateVolumeDiscount(unitPrice, quantity);

        // 100件应该有一定折扣
        assertTrue(discountedPrice.compareTo(unitPrice) < 0);
    }

    @Test
    void testCalculateTieredPrice() {
        // 测试阶梯定价
        List<PricingService.TierPricing> tiers = Arrays.asList(
            new PricingService.TierPricing(1, 10, new BigDecimal("10.00")),
            new PricingService.TierPricing(11, 50, new BigDecimal("9.00")),
            new PricingService.TierPricing(51, 100, new BigDecimal("8.00")),
            new PricingService.TierPricing(101, Integer.MAX_VALUE, new BigDecimal("7.00"))
        );

        // 测试第3档
        BigDecimal price75 = pricingService.calculateTieredPrice(75, tiers);
        assertEquals(new BigDecimal("8.00"), price75);

        // 测试第4档
        BigDecimal price150 = pricingService.calculateTieredPrice(150, tiers);
        assertEquals(new BigDecimal("7.00"), price150);
    }

    @Test
    void testCalculateBundlePrice() {
        // 测试捆绑定价
        List<PricingRequest.BundleItem> bundleItems = Arrays.asList(
            new PricingRequest.BundleItem(1001L, 2), // 产品1 x2
            new PricingRequest.BundleItem(1002L, 1)  // 产品2 x1
        );

        BigDecimal totalPrice = pricingService.calculateBundlePrice(bundleItems);

        assertNotNull(totalPrice);
        assertTrue(totalPrice.doubleValue() > 0);
    }

    @Test
    void testCalculateSeasonalAdjustment() {
        // 测试季节性调整
        LocalDateTime summer = LocalDateTime.of(2026, 7, 15, 12, 0);
        LocalDateTime winter = LocalDateTime.of(2026, 1, 15, 12, 0);

        BigDecimal basePrice = new BigDecimal("70.00");

        BigDecimal summerPrice = pricingService.calculateSeasonalAdjustment(basePrice, summer);
        BigDecimal winterPrice = pricingService.calculateSeasonalAdjustment(basePrice, winter);

        // 夏季应该比冬季价格高（空调、户外用品等）
        // 具体取决于产品类型，这里只验证有调整
        assertNotEquals(basePrice, summerPrice);
        assertNotEquals(basePrice, winterPrice);
    }

    @Test
    void testCalculateRegionalAdjustment() {
        // 测试地区调整
        BigDecimal basePrice = new BigDecimal("70.00");

        // 美国市场价格
        BigDecimal usPrice = pricingService.calculateRegionalAdjustment(
            basePrice, "US", new BigDecimal("1.0"));
        assertTrue(usPrice.compareTo(basePrice) > 0);

        // 日本市场价格（含消费税）
        BigDecimal jpPrice = pricingService.calculateRegionalAdjustment(
            basePrice, "JP", new BigDecimal("1.1"));
        assertTrue(jpPrice.compareTo(basePrice) > 0);
    }

    @Test
    void testAnalyzePriceHistoryTrend() {
        // 测试价格历史趋势分析
        List<PriceHistoryServiceImplTest.PricePoint> priceHistory = Arrays.asList(
            new PriceHistoryServiceImplTest.PricePoint(
                LocalDateTime.now().minusDays(30), new BigDecimal("65.00")),
            new PriceHistoryServiceImplTest.PricePoint(
                LocalDateTime.now().minusDays(20), new BigDecimal("68.00")),
            new PriceHistoryServiceImplTest.PricePoint(
                LocalDateTime.now().minusDays(10), new BigDecimal("70.00")),
            new PriceHistoryServiceImplTest.PricePoint(
                LocalDateTime.now(), new BigDecimal("72.00"))
        );

        String trend = pricingService.analyzePriceHistoryTrend(priceHistory);

        assertNotNull(trend);
        assertTrue(trend.equals("UP") || trend.equals("DOWN") || trend.equals("STABLE"));
    }

    // 辅助方法：创建竞品价格
    private CompetitorPriceInfo createCompetitorPrice(String name, BigDecimal price) {
        CompetitorPriceInfo info = new CompetitorPriceInfo();
        info.setCompetitorName(name);
        info.setPrice(price);
        return info;
    }
}