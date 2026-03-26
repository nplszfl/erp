package com.omnitrade.erp.social.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cooperation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cooperation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_id", nullable = false)
    private Influencer influencer;
    
    @Column(nullable = false)
    private String productId;
    
    @Column(nullable = false)
    private String productName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CooperationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CooperationStatus status;
    
    private LocalDateTime plannedStartDate;
    
    private LocalDateTime plannedEndDate;
    
    private LocalDateTime actualStartDate;
    
    private LocalDateTime actualEndDate;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal agreedPrice;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal actualCost;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal commissionRate;
    
    private Integer expectedViews;
    
    private Integer actualViews;
    
    private Integer expectedSales;
    
    private Integer actualSales;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal expectedGmv;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal actualGmv;
    
    @Column(length = 1000)
    private String content;
    
    @Column(length = 2000)
    private String remark;
    
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
    
    public enum CooperationType {
        LIVE, SHORT_VIDEO, POST, STORY, COLLABORATION
    }
    
    public enum CooperationStatus {
        DRAFT, PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}