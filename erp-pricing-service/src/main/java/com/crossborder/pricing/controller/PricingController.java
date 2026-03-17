package com.crossborder.pricing.controller;

import com.crossborder.pricing.dto.PricingRequest;
import com.crossborder.pricing.dto.PricingResponse;
import com.crossborder.pricing.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能定价控制器
 *
 * 提供定价相关API接口
 */
@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
@Tag(name = "智能定价", description = "智能定价相关接口")
public class PricingController {

    private final PricingService pricingService;

    @Operation(summary = "计算最优价格")
    @PostMapping("/calculate")
    public PricingResponse calculateOptimalPrice(@RequestBody PricingRequest request) {
        return pricingService.calculateOptimalPrice(request);
    }

    @Operation(summary = "批量计算最优价格")
    @PostMapping("/batch-calculate")
    public List<PricingResponse> batchCalculateOptimalPrice(@RequestBody List<PricingRequest> requests) {
        return pricingService.batchCalculateOptimalPrice(requests);
    }

    @Operation(summary = "根据竞品调整价格")
    @PostMapping("/adjust-by-competitors/{productId}")
    public PricingResponse adjustPriceByCompetitors(@PathVariable Long productId) {
        return pricingService.adjustPriceByCompetitors(productId);
    }

    @Operation(summary = "手动调整价格")
    @PostMapping("/manual-adjust")
    public PricingResponse manualPriceAdjustment(
            @RequestParam Long productId,
            @RequestParam java.math.BigDecimal targetPrice,
            @RequestParam String reason) {
        return pricingService.manualPriceAdjustment(productId, targetPrice, reason);
    }

    @Operation(summary = "获取产品定价信息")
    @GetMapping("/info/{productId}")
    public PricingResponse getProductPricingInfo(@PathVariable Long productId) {
        return pricingService.getProductPricingInfo(productId);
    }

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public String health() {
        return "Pricing Service is running! 🔥";
    }
}
