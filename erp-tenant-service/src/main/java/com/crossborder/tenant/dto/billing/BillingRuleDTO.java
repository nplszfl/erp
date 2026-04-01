package com.crossborder.tenant.dto.billing;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

/**
 * 计费规则DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingRuleDTO {

    private String id;
    private String name;
    private String resourceType;
    private String tierType;
    private Long freeQuota;
    private BigDecimal unitPrice;
    private String tierConfig;
    private Boolean active;
    private LocalDateTime createdAt;
}