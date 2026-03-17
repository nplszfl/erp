package com.crossborder.pricing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/api/v1/pricing")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "智能定价服务");
        response.put("version", "1.5.0");
        response.put("timestamp", "2026-03-17");
        response.put("message", "火球鼠的智能定价系统正在运行！🔥");
        response.put("features", Map.of(
            "competitorAnalysis", "竞品数据分析",
            "dynamicPricing", "动态定价策略",
            "pricePrediction", "价格预测",
            "batchProcessing", "批量处理"
        ));
        return response;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("serviceName", "erp-pricing-service");
        response.put("version", "1.5.0");
        response.put("author", "火球鼠");
        response.put("description", "AI驱动的智能定价系统");
        response.put("apiDocs", "http://localhost:8087/swagger-ui.html");
        response.put("endpoints", Map.of(
            "POST /api/v1/pricing/calculate", "计算最优价格",
            "POST /api/v1/pricing/batch-calculate", "批量计算最优价格",
            "POST /api/v1/pricing/adjust-by-competitors/{id}", "根据竞品调整价格",
            "GET /api/v1/pricing/info/{id}", "获取产品定价信息",
            "GET /api/v1/pricing/health", "健康检查"
        ));
        return response;
    }
}
