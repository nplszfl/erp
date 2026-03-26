package com.omnitrade.erp.social.controller;

import com.omnitrade.erp.social.dto.TikTokShopConfigDTO;
import com.omnitrade.erp.social.service.TikTokShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tiktok-shop")
@RequiredArgsConstructor
@Tag(name = "TikTok店铺管理", description = "TikTok Shop店铺配置和API集成")
public class TikTokShopController {
    
    private final TikTokShopService service;
    
    @GetMapping
    @Operation(summary = "获取所有TikTok店铺")
    public ResponseEntity<List<TikTokShopConfigDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取TikTok店铺详情")
    public ResponseEntity<TikTokShopConfigDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @GetMapping("/shop/{shopId}")
    @Operation(summary = "根据店铺ID获取")
    public ResponseEntity<TikTokShopConfigDTO> findByShopId(@PathVariable String shopId) {
        return ResponseEntity.ok(service.findByShopId(shopId));
    }
    
    @PostMapping
    @Operation(summary = "创建TikTok店铺配置")
    public ResponseEntity<TikTokShopConfigDTO> create(@Valid @RequestBody TikTokShopConfigDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新TikTok店铺配置")
    public ResponseEntity<TikTokShopConfigDTO> update(@PathVariable Long id, @RequestBody TikTokShopConfigDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除TikTok店铺配置")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/refresh-token")
    @Operation(summary = "刷新TikTok访问令牌")
    public ResponseEntity<TikTokShopConfigDTO> refreshToken(@PathVariable Long id) {
        return ResponseEntity.ok(service.refreshToken(id));
    }
}