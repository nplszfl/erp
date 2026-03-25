package com.crossborder.inventoryprediction.controller;

import com.crossborder.inventoryprediction.dto.AccuracyEvaluation;
import com.crossborder.inventoryprediction.dto.PredictionRequest;
import com.crossborder.inventoryprediction.dto.PredictionResponse;
import com.crossborder.inventoryprediction.dto.ReplenishmentSuggestion;
import com.crossborder.inventoryprediction.service.InventoryPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 库存预测控制器
 */
@RestController
@RequestMapping("/api/v1/inventory-prediction")
@RequiredArgsConstructor
@Tag(name = "库存预测", description = "库存预测和补货建议")
public class InventoryPredictionController {

    private final InventoryPredictionService inventoryPredictionService;

    @Operation(summary = "预测销量")
    @PostMapping("/predict")
    public PredictionResponse predictSales(@RequestBody PredictionRequest request) {
        return inventoryPredictionService.predictSales(request);
    }

    @Operation(summary = "批量预测销量")
    @PostMapping("/batch-predict")
    public List<PredictionResponse> batchPredictSales(@RequestBody List<PredictionRequest> requests) {
        return inventoryPredictionService.batchPredictSales(requests);
    }

    @Operation(summary = "获取补货建议")
    @GetMapping("/replenishment/{productId}")
    public ReplenishmentSuggestion getReplenishmentSuggestion(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "30") int predictionDays) {
        return inventoryPredictionService.getReplenishmentSuggestion(productId, predictionDays);
    }

    @Operation(summary = "计算安全库存")
    @GetMapping("/safety-stock/{productId}")
    public int calculateSafetyStock(@PathVariable Long productId) {
        return inventoryPredictionService.calculateSafetyStock(productId);
    }

    @Operation(summary = "预警库存不足")
    @GetMapping("/warn-low-stock")
    public List<Long> warnLowStock(@RequestParam(defaultValue = "30") int predictionDays) {
        return inventoryPredictionService.warnLowStock(predictionDays);
    }

    @Operation(summary = "计算库存周转率")
    @GetMapping("/turnover/{productId}")
    public double calculateInventoryTurnover(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "90") int days) {
        return inventoryPredictionService.calculateInventoryTurnover(productId, days);
    }

    @Operation(summary = "优化库存结构")
    @PostMapping("/optimize")
    public String optimizeInventoryStructure() {
        inventoryPredictionService.optimizeInventoryStructure();
        return "库存结构优化完成！";
    }

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public String health() {
        return "库存预测服务运行中！🔥";
    }

    @Operation(summary = "评估预测准确性")
    @GetMapping("/accuracy/{productId}")
    public AccuracyEvaluation evaluateAccuracy(
            @PathVariable Long productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return inventoryPredictionService.evaluateAccuracy(productId, startDate, endDate);
    }

    @Operation(summary = "批量评估预测准确性")
    @PostMapping("/accuracy/batch")
    public List<AccuracyEvaluation> batchEvaluateAccuracy(
            @RequestParam List<Long> productIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return inventoryPredictionService.batchEvaluateAccuracy(productIds, startDate, endDate);
    }

    @Operation(summary = "获取准确性趋势")
    @GetMapping("/accuracy-trend/{productId}")
    public List<AccuracyEvaluation> getAccuracyTrend(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "30") int days) {
        return inventoryPredictionService.getAccuracyTrend(productId, days);
    }
}
