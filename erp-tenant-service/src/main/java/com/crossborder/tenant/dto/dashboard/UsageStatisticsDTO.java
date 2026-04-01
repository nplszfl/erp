package com.crossborder.tenant.dto.dashboard;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 使用量统计DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageStatisticsDTO {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 统计周期
     */
    private String billingPeriod;

    /**
     * API调用
     */
    private Long apiCalls;
    private Long apiCallsLimit;
    private Double apiCallsPercent;

    /**
     * 存储用量
     */
    private Long storageBytes;
    private Long storageLimit;
    private Double storagePercent;
    private String storageDisplay; // 格式化显示

    /**
     * 订单数量
     */
    private Integer orderCount;
    private Integer orderLimit;
    private Double orderPercent;

    /**
     * 平台数量
     */
    private Integer platformCount;
    private Integer platformLimit;
    private Double platformPercent;

    /**
     * AI调用
     */
    private Long aiCalls;
    private Long aiCallsLimit;
    private Double aiCallsPercent;

    /**
     * 每日趋势（最近7天）
     */
    private java.util.List<DailyUsageDTO> dailyTrend;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}