package com.omnitrade.erp.social.controller;

import com.omnitrade.erp.social.dto.LiveStreamDTO;
import com.omnitrade.erp.social.service.LiveStreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/live-streams")
@RequiredArgsConstructor
@Tag(name = "直播管理", description = "直播带货管理")
public class LiveStreamController {
    
    private final LiveStreamService service;
    
    @GetMapping
    @Operation(summary = "获取所有直播")
    public ResponseEntity<List<LiveStreamDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取直播详情")
    public ResponseEntity<LiveStreamDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @GetMapping("/stream/{streamId}")
    @Operation(summary = "根据直播ID获取")
    public ResponseEntity<LiveStreamDTO> findByStreamId(@PathVariable String streamId) {
        return ResponseEntity.ok(service.findByStreamId(streamId));
    }
    
    @GetMapping("/platform/{platform}")
    @Operation(summary = "按平台查询直播")
    public ResponseEntity<List<LiveStreamDTO>> findByPlatform(@PathVariable String platform) {
        return ResponseEntity.ok(service.findByPlatform(platform));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询直播")
    public ResponseEntity<List<LiveStreamDTO>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.findByStatus(status));
    }
    
    @GetMapping("/influencer/{influencerId}")
    @Operation(summary = "查询达人的直播")
    public ResponseEntity<List<LiveStreamDTO>> findByInfluencerId(@PathVariable Long influencerId) {
        return ResponseEntity.ok(service.findByInfluencerId(influencerId));
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "按日期范围查询直播")
    public ResponseEntity<List<LiveStreamDTO>> findByDateRange(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        return ResponseEntity.ok(service.findByDateRange(startTime, endTime));
    }
    
    @GetMapping("/current-live")
    @Operation(summary = "获取当前直播中")
    public ResponseEntity<List<LiveStreamDTO>> findCurrentLive() {
        return ResponseEntity.ok(service.findCurrentLive());
    }
    
    @GetMapping("/recent")
    @Operation(summary = "获取最近直播")
    public ResponseEntity<List<LiveStreamDTO>> findRecent() {
        return ResponseEntity.ok(service.findRecent());
    }
    
    @PostMapping
    @Operation(summary = "创建直播")
    public ResponseEntity<LiveStreamDTO> create(@Valid @RequestBody LiveStreamDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新直播信息")
    public ResponseEntity<LiveStreamDTO> update(@PathVariable Long id, @RequestBody LiveStreamDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    
    @PostMapping("/{id}/start")
    @Operation(summary = "开始直播")
    public ResponseEntity<LiveStreamDTO> startLive(@PathVariable Long id) {
        return ResponseEntity.ok(service.startLive(id));
    }
    
    @PostMapping("/{id}/end")
    @Operation(summary = "结束直播")
    public ResponseEntity<LiveStreamDTO> endLive(@PathVariable Long id, @RequestBody(required = false) LiveStreamDTO dto) {
        return ResponseEntity.ok(service.endLive(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除直播")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}