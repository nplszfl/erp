package com.crossborder.tenant.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "企业名称不能为空")
    @Size(min = 2, max = 100)
    private String companyName;
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50)
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100)
    private String password;
    
    private String phone;
    private String plan = "STARTER";
}