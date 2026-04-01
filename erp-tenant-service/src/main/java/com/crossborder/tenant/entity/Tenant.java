package com.crossborder.tenant.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 租户实体 - SaaS多租户核心
 */
@Entity
@Table(name = "tenants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 企业名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 企业域名（自定义）
     */
    @Column(unique = true)
    private String domain;

    /**
     * 订阅计划
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TenantPlan plan;

    /**
     * 租户状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TenantStatus status;

    /**
     * 数据区域
     */
    private String region;

    /**
     * 租户配置（JSON）
     */
    @Column(columnDefinition = "TEXT")
    private String settings;

    /**
     * 到期时间
     */
    private LocalDateTime expiresAt;

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
}