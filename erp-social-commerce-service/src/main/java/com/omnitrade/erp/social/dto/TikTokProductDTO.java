package com.omnitrade.erp.social.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TikTok 商品DTO
 */
@Data
@Builder
public class TikTokProductDTO {
    
    private Long id;
    private String productId;
    private String shopId;
    private String productName;
    private String description;
    private String categoryId;
    private String categoryName;
    private String status;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String currency;
    private Integer stockQuantity;
    private String mainImageUrl;
    private String imageUrls;
    private String videoUrl;
    private String skus;
    private String attributes;
    private Integer weight;
    private Boolean supportCod;
    private Boolean isLiveProduct;
    private BigDecimal liveCommissionRate;
    private Integer salesCount;
    private Integer viewCount;
    private Integer favoriteCount;
    private BigDecimal rating;
    private Integer reviewCount;
    private String localProductId;
    private String externalProductId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime publishTime;
    private String createdAt;
    private String updatedAt;
}