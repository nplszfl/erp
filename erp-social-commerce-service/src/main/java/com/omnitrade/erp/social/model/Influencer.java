package com.omnitrade.erp.social.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "influencer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Influencer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true)
    private String platformId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;
    
    @Column(length = 500)
    private String avatarUrl;
    
    private Integer followerCount;
    
    private Integer followingCount;
    
    private Integer totalViews;
    
    private Integer totalLikes;
    
    @Enumerated(EnumType.STRING)
    private InfluencerLevel level;
    
    @Enumerated(EnumType.STRING)
    private InfluencerStatus status;
    
    private String contactEmail;
    
    private String contactPhone;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal cooperationPrice;
    
    @Column(length = 1000)
    private String categories;
    
    @Column(length = 2000)
    private String description;
    
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
    
    public enum Platform {
        TIKTOK, DOUYIN, INSTAGRAM, YOUTUBE
    }
    
    public enum InfluencerLevel {
        KOC, KOL, MAK
    }
    
    public enum InfluencerStatus {
        ACTIVE, INACTIVE, BLACKLIST
    }
}