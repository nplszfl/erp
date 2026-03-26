package com.omnitrade.erp.social.controller;

import com.omnitrade.erp.social.model.DouyinShopConfig;
import com.omnitrade.erp.social.service.DouyinShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/douyin-shop")
@RequiredArgsConstructor
@Tag(name = "抖音店铺管理", description = "抖音店铺配置和API集成")
public class DouyinShopController {
    
    private final DouyinShopService service;
    
    @GetMapping
    @Operation(summary = "获取所有抖音店铺")
    public ResponseEntity<List<DouyinShopConfig>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取抖音店铺详情")
    public ResponseEntity<DouyinShopConfig> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @GetMapping("/shop/{shopId}")
    @Operation(summary = "根据店铺ID获取")
    public ResponseEntity<DouyinShopConfig> findByShopId(@PathVariable String shopId) {
        return ResponseEntity.ok(service.findByShopId(shopId));
    }
    
    @PostMapping
    @Operation(summary = "创建抖音店铺配置")
    public ResponseEntity<DouyinShopConfig> create(@RequestBody DouyinShopConfig entity) {
        return ResponseEntity.ok(service.create(entity));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新抖音店铺配置")
    public ResponseEntity<DouyinShopConfig> update(@PathVariable Long id, @RequestBody DouyinShopConfig entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除抖音店铺配置")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/refresh-token")
    @Operation(summary = "刷新抖音访问令牌")
    public ResponseEntity<DouyinShopConfig> refreshToken(@PathVariable Long id) {
        return ResponseEntity.ok(service.refreshToken(id));
    }
}