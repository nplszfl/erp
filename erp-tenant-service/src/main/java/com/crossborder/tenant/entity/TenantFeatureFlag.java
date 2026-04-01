package com.crossborder.tenant.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 租户功能开关配置
 */
@Entity
@Table(name = "tenant_feature_flags")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantFeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 租户ID
     */
    @Column(nullable = false)
    private String tenantId;

    /**
     * 功能标识
     */
    @Column(nullable = false)
    private String featureKey;

    /**
     * 功能名称
     */
    private String featureName;

    /**
     * 是否启用
     */
    @Builder.Default
    private Boolean enabled = false;

    /**
     * 功能配置（JSON）
     */
    @Column(columnDefinition = "TEXT")
    private String config;

    /**
     * 备注
     */
    private String description;

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

    /**
     * 默认功能列表
     */
    public static final Map<String, String> DEFAULT_FEATURES = new HashMap<>();
    static {
        DEFAULT_FEATURES.put("AI_ASSISTANT", "AI智能助手");
        DEFAULT_FEATURES.put("AUTO_SYNC", "自动同步");
        DEFAULT_FEATURES.put("ADVANCED_REPORT", "高级报表");
        DEFAULT_FEATURES.put("API_ACCESS", "API访问");
        DEFAULT_FEATURES.put("WHITE_LABEL", "白标定制");
        DEFAULT_FEATURES.put("MULTI_USER", "多用户协作");
        DEFAULT_FEATURES.put("WEBHOOK", "Webhooks");
        DEFAULT_FEATURES.put("EXPORT_EXCEL", "Excel导出");
    }
}