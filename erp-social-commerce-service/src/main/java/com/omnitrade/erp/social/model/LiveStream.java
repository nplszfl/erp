package com.omnitrade.erp.social.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "live_stream")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveStream {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String streamId;
    
    @Column(nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_id")
    private Influencer influencer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StreamStatus status;
    
    private LocalDateTime scheduledStartTime;
    
    private LocalDateTime actualStartTime;
    
    private LocalDateTime endTime;
    
    private Integer duration;
    
    private Integer viewerCount;
    
    private Integer peakViewers;
    
    private Integer likeCount;
    
    private Integer shareCount;
    
    private Integer commentCount;
    
    private Integer newFollowers;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal totalSales;
    
    private Integer totalOrders;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal totalGmv;
    
    @Column(length = 500)
    private String coverImageUrl;
    
    @Column(length = 1000)
    private String products;
    
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
        TIKTOK, DOUYIN, TAOBAO_LIVE
    }
    
    public enum StreamStatus {
        SCHEDULED, LIVE, ENDED, CANCELLED
    }
}