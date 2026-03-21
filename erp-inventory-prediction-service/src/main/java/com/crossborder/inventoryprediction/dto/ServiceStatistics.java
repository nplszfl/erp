package com.crossborder.inventoryprediction.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 库存预测服务统计
 */
@Data
public class ServiceStatistics {

    /** 总预测次数 */
    private long totalPredictions;

    /** 成功预测次数 */
    private long successfulPredictions;

    /** 失败预测次数 */
    private long failedPredictions;

    /** 补货建议生成次数 */
    private long replenishmentSuggestions;

    /** 库存预警次数 */
    private long lowStockWarnings;

    /** 平均预测时间（毫秒） */
    private double avgPredictionTime;

    /** 服务启动时间 */
    private LocalDateTime startTime;

    /** 统计时间 */
    private LocalDateTime statisticsTime;

    /** 错误信息列表 */
    private java.util.List<String> recentErrors;

    public ServiceStatistics() {
        this.startTime = LocalDateTime.now();
        this.statisticsTime = LocalDateTime.now();
        this.recentErrors = new java.util.ArrayList<>();
    }

    /**
     * 计算成功率
     */
    public double getSuccessRate() {
        if (totalPredictions == 0) {
            return 0.0;
        }
        return (double) successfulPredictions / totalPredictions * 100;
    }

    /**
     * 计算服务运行时长（分钟）
     */
    public long getUptimeMinutes() {
        return java.time.Duration.between(startTime, LocalDateTime.now()).toMinutes();
    }
}