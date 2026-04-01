package com.crossborder.tenant.dto;

import lombok.*;
import java.util.Map;

/**
 * 套餐变更请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanChangeRequest {

    /**
     * 目标套餐
     */
    private String targetPlan;

    /**
     * 变更类型：UPGRADE, DOWNGRADE, RENEW
     */
    private String changeType;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 备注
     */
    private String remark;
}