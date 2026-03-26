package com.omnitrade.erp.social.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TikTokShopConfigDTO {
    
    private Long id;
    
    @NotBlank(message = "店铺名称不能为空")
    private String shopName;
    
    @NotBlank(message = "店铺ID不能为空")
    private String shopId;
    
    private String accessToken;
    
    private String refreshToken;
    
    private String tokenExpireTime;
    
    private String appKey;
    
    private String appSecret;
    
    private String status;
    
    private String createdAt;
    private String updatedAt;
}