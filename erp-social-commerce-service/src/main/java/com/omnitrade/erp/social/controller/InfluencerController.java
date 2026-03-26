package com.omnitrade.erp.social.controller;

import com.omnitrade.erp.social.dto.InfluencerDTO;
import com.omnitrade.erp.social.service.InfluencerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/influencers")
@RequiredArgsConstructor
@Tag(name = "达人管理", description = "社交电商达人信息管理")
public class InfluencerController {
    
    private final InfluencerService service;
    
    @GetMapping
    @Operation(summary = "获取所有达人")
    public ResponseEntity<List<InfluencerDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取达人详情")
    public ResponseEntity<InfluencerDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @GetMapping("/platform/{platform}")
    @Operation(summary = "按平台查询达人")
    public ResponseEntity<List<InfluencerDTO>> findByPlatform(@PathVariable String platform) {
        return ResponseEntity.ok(service.findByPlatform(platform));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询达人")
    public ResponseEntity<List<InfluencerDTO>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.findByStatus(status));
    }
    
    @GetMapping("/level/{level}")
    @Operation(summary = "按级别查询达人")
    public ResponseEntity<List<InfluencerDTO>> findByLevel(@PathVariable String level) {
        return ResponseEntity.ok(service.findByLevel(level));
    }
    
    @GetMapping("/top")
    @Operation(summary = "获取头部达人")
    public ResponseEntity<List<InfluencerDTO>> findTopInfluencers(
            @RequestParam(defaultValue = "10000") Integer minFollowers) {
        return ResponseEntity.ok(service.findTopInfluencers(minFollowers));
    }
    
    @GetMapping("/category")
    @Operation(summary = "按分类查询达人")
    public ResponseEntity<List<InfluencerDTO>> findByCategory(@RequestParam String category) {
        return ResponseEntity.ok(service.findByCategory(category));
    }
    
    @PostMapping
    @Operation(summary = "创建达人")
    public ResponseEntity<InfluencerDTO> create(@Valid @RequestBody InfluencerDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新达人信息")
    public ResponseEntity<InfluencerDTO> update(@PathVariable Long id, @RequestBody InfluencerDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除达人")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}