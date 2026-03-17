package com.crossborder.pricing.controller;

import com.crossborder.pricing.entity.PriceHistory;
import com.crossborder.pricing.service.PriceHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 价格历史控制器
 */
@RestController
@RequestMapping("/api/v1/price-history")
@RequiredArgsConstructor
@Tag(name = "价格历史", description = "价格历史记录和趋势分析")
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    @Operation(summary = "获取产品价格历史")
    @GetMapping("/list/{productId}")
    public List<PriceHistory> getPriceHistory(@PathVariable Long productId) {
        return priceHistoryService.getPriceHistory(productId);
    }

    @Operation(summary = "获取指定时间范围的价格历史")
    @GetMapping("/range/{productId}")
    public List<PriceHistory> getPriceHistory(
            @PathVariable Long productId,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        return priceHistoryService.getPriceHistory(productId, start, end);
    }

    @Operation(summary = "获取最近N次价格变更")
    @GetMapping("/recent/{productId}")
    public List<PriceHistory> getRecentPriceChanges(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") int limit) {
        return priceHistoryService.getRecentPriceChanges(productId, limit);
    }

    @Operation(summary = "获取价格趋势分析")
    @GetMapping("/trend/{productId}")
    public PriceHistoryService.PriceTrendAnalysis getPriceTrendAnalysis(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "30") int days) {
        return priceHistoryService.getPriceTrendAnalysis(productId, days);
    }
}
