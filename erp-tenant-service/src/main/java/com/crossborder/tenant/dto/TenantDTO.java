package com.crossborder.tenant.dto;

import com.crossborder.tenant.entity.TenantPlan;
import com.crossborder.tenant.entity.TenantStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDTO {
    private String id;
    
    @NotBlank(message = "企业名称不能为空")
    @Size(min = 2, max = 100, message = "企业名称长度2-100")
    private String name;

    @Size(max = 100, message = "域名长度最大100")
    private String domain;

    @NotNull(message = "订阅计划不能为空")
    private TenantPlan plan;

    private TenantStatus status;
    private String region;
    private String settings;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}