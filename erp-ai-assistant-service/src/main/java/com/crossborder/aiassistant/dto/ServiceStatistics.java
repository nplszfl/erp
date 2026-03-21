package com.crossborder.aiassistant.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI服务统计数据
 */
@Data
public class ServiceStatistics {

    /** 总对话次数 */
    private long totalConversations;

    /** 成功对话次数 */
    private long successfulConversations;

    /** 失败对话次数 */
    private long failedConversations;

    /** 成功调用LLM API次数 */
    private long llmApiCalls;

    /** 使用模拟回复次数 */
    private long mockResponses;

    /** 总Token消耗（估算） */
    private long totalTokens;

    /** 平均响应时间（毫秒） */
    private double avgResponseTime;

    /** 最大响应时间（毫秒） */
    private long maxResponseTime;

    /** 最小响应时间（毫秒） */
    private long minResponseTime;

    /** 活跃用户数 */
    private long activeUsers;

    /** 活跃会话数 */
    private long activeSessions;

    /** RAG检索次数 */
    private long ragRetrievals;

    /** 知识库条目数 */
    private long knowledgeBaseSize;

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
        if (totalConversations == 0) {
            return 0.0;
        }
        return (double) successfulConversations / totalConversations * 100;
    }

    /**
     * 计算错误率
     */
    public double getErrorRate() {
        if (totalConversations == 0) {
            return 0.0;
        }
        return (double) failedConversations / totalConversations * 100;
    }

    /**
     * 计算服务运行时长（分钟）
     */
    public long getUptimeMinutes() {
        return java.time.Duration.between(startTime, LocalDateTime.now()).toMinutes();
    }
}