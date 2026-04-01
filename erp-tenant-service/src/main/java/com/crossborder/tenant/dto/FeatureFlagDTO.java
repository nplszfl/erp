package com.crossborder.tenant.dto;

import lombok.*;

/**
 * 功能开关配置DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagDTO {

    private String id;
    private String tenantId;
    private String featureKey;
    private String featureName;
    private Boolean enabled;
    private String config;
    private String description;
}