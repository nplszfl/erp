package com.crossborder.productdescription.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 产品描述服务统计数据
 */
@Data
public class ServiceStatistics {

    /** 总生成次数 */
    private long totalGenerations;

    /** 成功生成次数 */
    private long successfulGenerations;

    /** 失败生成次数 */
    private long failedGenerations;

    /** 成功调用LLM API次数 */
    private long llmApiCalls;

    /** 使用模拟回复次数 */
    private long mockResponses;

    /** 总Token消耗（估算） */
    private long totalTokens;

    /** 平均生成时间（毫秒） */
    private double avgGenerationTime;

    /** 最大生成时间（毫秒） */
    private long maxGenerationTime;

    /** 最小生成时间（毫秒） */
    private long minGenerationTime;

    /** 各平台生成数量统计 */
    private java.util.Map<String, Long> platformStats;

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
        this.platformStats = new java.util.HashMap<>();
    }

    /**
     * 计算成功率
     */
    public double getSuccessRate() {
        if (totalGenerations == 0) {
            return 0.0;
        }
        return (double) successfulGenerations / totalGenerations * 100;
    }

    /**
     * 计算服务运行时长（分钟）
     */
    public long getUptimeMinutes() {
        return java.time.Duration.between(startTime, LocalDateTime.now()).toMinutes();
    }
}