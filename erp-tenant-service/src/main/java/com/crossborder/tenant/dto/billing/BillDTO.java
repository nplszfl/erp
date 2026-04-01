package com.crossborder.tenant.dto.billing;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

/**
 * 账单DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillDTO {

    private String id;
    private String tenantId;
    private YearMonth billingPeriod;
    private String billType;
    private String status;
    private BigDecimal subscriptionFee;
    private BigDecimal usageFee;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String paymentMethod;
    private LocalDateTime paidAt;
    private LocalDateTime dueDate;
    private String details;
    private String remark;
    private LocalDateTime createdAt;
}