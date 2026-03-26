package com.omnitrade.erp.social.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tiktok_shop_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TikTokShopConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String shopName;
    
    @Column(unique = true, nullable = false)
    private String shopId;
    
    @Column(length = 500)
    private String accessToken;
    
    @Column(length = 500)
    private String refreshToken;
    
    private LocalDateTime tokenExpireTime;
    
    @Column(length = 100)
    private String appKey;
    
    @Column(length = 100)
    private String appSecret;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConfigStatus status;
    
    private LocalDateTime createdAt;
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
    
    public enum ConfigStatus {
        ACTIVE, INACTIVE, EXPIRED, ERROR
    }
}