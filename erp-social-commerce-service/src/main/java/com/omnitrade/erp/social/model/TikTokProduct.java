package com.omnitrade.erp.social.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TikTok 商品实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tiktok_product")
public class TikTokProduct {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * TikTok商品ID
     */
    @Column(unique = true, nullable = false)
    private String productId;
    
    /**
     * 店铺ID
     */
    @Column(nullable = false)
    private String shopId;
    
    /**
     * 商品名称
     */
    @Column(nullable = false)
    private String productName;
    
    /**
     * 商品描述
     */
    @Column(length = 2000)
    private String description;
    
    /**
     * 商品类目ID
     */
    private String categoryId;
    
    /**
     * 商品类目名称
     */
    private String categoryName;
    
    /**
     * 商品状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;
    
    /**
     * 商品价格
     */
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
    
    /**
     * 商品原价
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice;
    
    /**
     * 货币单位
     */
    private String currency;
    
    /**
     * 库存数量
     */
    private Integer stockQuantity;
    
    /**
     * 商品主图URL
     */
    @Column(length = 500)
    private String mainImageUrl;
    
    /**
     * 商品图片URLs (JSON数组)
     */
    @Column(length = 2000)
    private String imageUrls;
    
    /**
     * 商品视频URL
     */
    @Column(length = 500)
    private String videoUrl;
    
    /**
     * SKU信息 (JSON格式)
     */
    @Column(length = 3000)
    private String skus;
    
    /**
     * 商品属性 (JSON格式)
     */
    @Column(length = 2000)
    private String attributes;
    
    /**
     * 商品重量 (克)
     */
    private Integer weight;
    
    /**
     * 是否支持COD (货到付款)
     */
    private Boolean supportCod;
    
    /**
     * 是否为直播商品
     */
    private Boolean isLiveProduct;
    
    /**
     * 直播商品佣金率
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal liveCommissionRate;
    
    /**
     * 销量
     */
    private Integer salesCount;
    
    /**
     * 浏览量
     */
    private Integer viewCount;
    
    /**
     * 收藏数
     */
    private Integer favoriteCount;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 评论数
     */
    private Integer reviewCount;
    
    /**
     * 本地ERP商品ID
     */
    private String localProductId;
    
    /**
     * 外部商品ID (ERP系统)
     */
    private String externalProductId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 上架时间
     */
    private LocalDateTime publishTime;
    
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
    
    public enum ProductStatus {
        DRAFT,          // 草稿
        PENDING,        // 待审核
        ACTIVE,         // 上架
        INACTIVE,       // 下架
        DELETED,        // 已删除
        REJECTED        // 审核拒绝
    }
}