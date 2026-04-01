package com.crossborder.tenant.controller;

import com.crossborder.tenant.dto.*;
import com.crossborder.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户管理控制器
 */
@RestController
@RequestMapping("/api/v1/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * 获取当前租户信息
     */
    @GetMapping
    public ResponseEntity<TenantDTO> getTenant() {
        return ResponseEntity.ok(tenantService.getCurrentTenant());
    }

    /**
     * 获取租户用户列表
     */
    @GetMapping("/users")
    public ResponseEntity<List<TenantUserDTO>> getUsers() {
        return ResponseEntity.ok(tenantService.getTenantUsers());
    }

    /**
     * 升级订阅计划
     */
    @PostMapping("/upgrade")
    public ResponseEntity<TenantDTO> upgradePlan(@RequestParam String plan) {
        return ResponseEntity.ok(tenantService.upgradePlan(plan));
    }
}