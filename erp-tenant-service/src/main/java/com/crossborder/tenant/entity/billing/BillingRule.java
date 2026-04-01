package com.crossborder.tenant.entity.billing;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

/**
 * 计费规则
 */
@Entity
@Table(name = "billing_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 规则名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 资源类型：API_CALLS, STORAGE, ORDERS, PLATFORMS, AI_CALLS
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType resourceType;

    /**
     * 阶梯类型：TIERED（阶梯）, VOLUME（总量）, FIXED（固定）
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TierType tierType;

    /**
     * 免费额度
     */
    @Builder.Default
    private Long freeQuota = 0L;

    /**
     * 超额单价（每单位）
     */
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal unitPrice;

    /**
     * 阶梯配置（JSON）
     */
    @Column(columnDefinition = "TEXT")
    private String tierConfig;

    /**
     * 生效状态
     */
    @Builder.Default
    private Boolean active = true;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ResourceType {
        API_CALLS, STORAGE, ORDERS, PLATFORMS, AI_CALLS
    }

    public enum TierType {
        TIERED, VOLUME, FIXED
    }
}