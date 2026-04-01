package com.crossborder.tenant.dto.dashboard;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 租户仪表盘数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    /**
     * 租户基本信息
     */
    private String tenantId;
    private String tenantName;
    private String plan;
    private String status;

    /**
     * 使用量概览
     */
    private Long apiCalls;
    private Long storageBytes;
    private Integer orderCount;
    private Integer platformCount;
    private Long aiCalls;

    /**
     * 配额使用情况
     */
    private Integer apiCallsLimit;
    private Long storageLimit;
    private Integer orderLimit;
    private Integer platformLimit;
    private Long aiCallsLimit;

    /**
     * 使用百分比
     */
    private Double apiCallsPercent;
    private Double storagePercent;
    private Double orderPercent;
    private Double platformPercent;
    private Double aiCallsPercent;

    /**
     * 本月账单
     */
    private Double currentBill;
    private String billStatus;

    /**
     * 近期活动
     */
    private Integer activeUsers;
    private LocalDateTime lastActivity;

    /**
     * 到期信息
     */
    private LocalDateTime expiresAt;
    private Integer daysRemaining;

    /**
     * 是否即将到期
     */
    private Boolean expiringSoon;
}