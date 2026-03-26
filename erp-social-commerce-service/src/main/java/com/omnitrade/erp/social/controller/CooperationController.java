package com.omnitrade.erp.social.controller;

import com.omnitrade.erp.social.dto.CooperationDTO;
import com.omnitrade.erp.social.service.CooperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cooperations")
@RequiredArgsConstructor
@Tag(name = "达人合作管理", description = "达人合作项目管理")
public class CooperationController {
    
    private final CooperationService service;
    
    @GetMapping
    @Operation(summary = "获取所有合作")
    public ResponseEntity<List<CooperationDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取合作详情")
    public ResponseEntity<CooperationDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @GetMapping("/influencer/{influencerId}")
    @Operation(summary = "查询达人的合作")
    public ResponseEntity<List<CooperationDTO>> findByInfluencerId(@PathVariable Long influencerId) {
        return ResponseEntity.ok(service.findByInfluencerId(influencerId));
    }
    
    @GetMapping("/product/{productId}")
    @Operation(summary = "查询产品的合作")
    public ResponseEntity<List<CooperationDTO>> findByProductId(@PathVariable String productId) {
        return ResponseEntity.ok(service.findByProductId(productId));
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "按类型查询合作")
    public ResponseEntity<List<CooperationDTO>> findByType(@PathVariable String type) {
        return ResponseEntity.ok(service.findByType(type));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询合作")
    public ResponseEntity<List<CooperationDTO>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.findByStatus(status));
    }
    
    @GetMapping("/completed")
    @Operation(summary = "获取已完成的合作")
    public ResponseEntity<List<CooperationDTO>> findCompleted() {
        return ResponseEntity.ok(service.findCompleted());
    }
    
    @GetMapping("/upcoming")
    @Operation(summary = "获取即将进行的合作")
    public ResponseEntity<List<CooperationDTO>> findUpcoming() {
        return ResponseEntity.ok(service.findUpcoming());
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "按日期范围查询合作")
    public ResponseEntity<List<CooperationDTO>> findByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(service.findByDateRange(startDate, endDate));
    }
    
    @PostMapping
    @Operation(summary = "创建合作")
    public ResponseEntity<CooperationDTO> create(@Valid @RequestBody CooperationDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新合作信息")
    public ResponseEntity<CooperationDTO> update(@PathVariable Long id, @RequestBody CooperationDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    
    @PostMapping("/{id}/confirm")
    @Operation(summary = "确认合作")
    public ResponseEntity<CooperationDTO> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirm(id));
    }
    
    @PostMapping("/{id}/start")
    @Operation(summary = "开始合作")
    public ResponseEntity<CooperationDTO> start(@PathVariable Long id) {
        return ResponseEntity.ok(service.start(id));
    }
    
    @PostMapping("/{id}/complete")
    @Operation(summary = "完成合作")
    public ResponseEntity<CooperationDTO> complete(@PathVariable Long id, @RequestBody(required = false) CooperationDTO dto) {
        return ResponseEntity.ok(service.complete(id, dto));
    }
    
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消合作")
    public ResponseEntity<CooperationDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancel(id));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除合作")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}