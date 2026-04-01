package com.crossborder.tenant.controller;

import com.crossborder.tenant.dto.*;
import com.crossborder.tenant.dto.auth.AuthResponse;
import com.crossborder.tenant.dto.auth.LoginRequest;
import com.crossborder.tenant.dto.auth.RegisterRequest;
import com.crossborder.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证控制器 - 注册/登录
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TenantService tenantService;

    /**
     * 注册新租户
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(tenantService.register(request));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(tenantService.login(request.getUsername(), request.getPassword()));
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<TenantUserDTO> getCurrentUser() {
        return ResponseEntity.ok(tenantService.getCurrentUser());
    }
}