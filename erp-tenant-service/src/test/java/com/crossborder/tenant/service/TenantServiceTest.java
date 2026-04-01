package com.crossborder.tenant.service;

import com.crossborder.tenant.dto.AuthDTO.*;
import com.crossborder.tenant.dto.RegisterRequest;
import com.crossborder.tenant.entity.TenantPlan;
import com.crossborder.tenant.entity.TenantStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TenantServiceTest {

    @Autowired
    private TenantService tenantService;

    @Test
    void testRegister() {
        RegisterRequest request = RegisterRequest.builder()
                .companyName("测试公司")
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .plan("STARTER")
                .build();

        AuthResponse response = tenantService.register(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getUser());
        assertNotNull(response.getTenant());
        assertEquals("testuser", response.getUser().getUsername());
        assertEquals("测试公司", response.getTenant().getName());
    }

    @Test
    void testLogin() {
        // 先注册
        RegisterRequest registerRequest = RegisterRequest.builder()
                .companyName("登录测试公司")
                .username("logintest")
                .email("login@test.com")
                .password("password123")
                .plan("PROFESSIONAL")
                .build();
        tenantService.register(registerRequest);

        // 再登录
        AuthResponse response = tenantService.login("logintest", "password123");

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("logintest", response.getUser().getUsername());
        assertEquals(TenantPlan.PROFESSIONAL, response.getTenant().getPlan());
        assertEquals(TenantStatus.ACTIVE, response.getTenant().getStatus());
    }

    @Test
    void testLoginFail() {
        assertThrows(RuntimeException.class, () -> {
            tenantService.login("nonexistent", "wrongpassword");
        });
    }
}