package com.crossborder.tenant.entity.platform;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 租户平台凭证 - 每个租户可配置多个平台店铺
 */
@Entity
@Table(name = "platform_credentials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 租户ID
     */
    @Column(nullable = false)
    private String tenantId;

    /**
     * 平台类型（amazon/ebay/shopee/lazada/tiktok）
     */
    @Column(nullable = false)
    private String platform;

    /**
     * 店铺名称
     */
    @Column(nullable = false)
    private String shopName;

    /**
     * 店铺ID
     */
    private String shopId;

    /**
     * 加密存储的凭证
     */
    @Column(columnDefinition = "TEXT")
    private String credentials; // 加密存储

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 客户端密钥（加密存储）
     */
    private String clientSecret;

    /**
     * 开发者ID
     */
    private String developerId;

    /**
     * Refresh Token
     */
    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    /**
     * 访问令牌
     */
    @Column(columnDefinition = "TEXT")
    private String accessToken;

    /**
     * 令牌过期时间
     */
    private LocalDateTime tokenExpiresAt;

    /**
     * 市场/站点
     */
    private String marketplace;

    /**
     * 状态（ACTIVE/INACTIVE/ERROR）
     */
    @Enumerated(EnumType.STRING)
    private CredentialStatus status;

    /**
     * 最后同步时间
     */
    private LocalDateTime lastSyncAt;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = CredentialStatus.ACTIVE;
    }
}

