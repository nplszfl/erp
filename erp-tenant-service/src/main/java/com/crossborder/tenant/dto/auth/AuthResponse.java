package com.crossborder.tenant.dto.auth;

import com.crossborder.tenant.dto.TenantDTO;
import com.crossborder.tenant.dto.TenantUserDTO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private TenantUserDTO user;
    private TenantDTO tenant;
}