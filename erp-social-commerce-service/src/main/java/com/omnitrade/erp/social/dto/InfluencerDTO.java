package com.omnitrade.erp.social.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfluencerDTO {
    
    private Long id;
    
    @NotBlank(message = "达人名称不能为空")
    private String name;
    
    private String platformId;
    
    @NotNull(message = "平台不能为空")
    private String platform;
    
    private String avatarUrl;
    
    private Integer followerCount;
    
    private Integer followingCount;
    
    private Integer totalViews;
    
    private Integer totalLikes;
    
    private String level;
    
    private String status;
    
    private String contactEmail;
    
    private String contactPhone;
    
    private BigDecimal cooperationPrice;
    
    private String categories;
    
    private String description;
    
    private String createdAt;
    private String updatedAt;
}