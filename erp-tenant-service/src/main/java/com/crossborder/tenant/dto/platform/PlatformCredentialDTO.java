package com.crossborder.tenant.dto.platform;

import com.crossborder.tenant.entity.platform.CredentialStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformCredentialDTO {
    private String id;
    private String tenantId;
    
    @NotBlank(message = "平台不能为空")
    private String platform;
    
    @NotBlank(message = "店铺名称不能为空")
    private String shopName;
    private String shopId;
    private String clientId;
    private String clientSecret;
    private String developerId;
    private String refreshToken;
    private String marketplace;
    private CredentialStatus status;
    private LocalDateTime lastSyncAt;
    private LocalDateTime createdAt;
    private String accessToken;
    private LocalDateTime tokenExpiresAt;
}