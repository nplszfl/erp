package com.crossborder.tenant.controller;

import com.crossborder.tenant.dto.platform.PlatformCredentialDTO;
import com.crossborder.tenant.service.PlatformCredentialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台凭证控制器 - 管理租户的店铺连接
 */
@RestController
@RequestMapping("/api/v1/platforms")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformCredentialService credentialService;

    /**
     * 获取所有平台凭证
     */
    @GetMapping("/credentials")
    public ResponseEntity<List<PlatformCredentialDTO>> getCredentials() {
        return ResponseEntity.ok(credentialService.getCredentials());
    }

    /**
     * 获取指定平台凭证
     */
    @GetMapping("/credentials/{platform}")
    public ResponseEntity<List<PlatformCredentialDTO>> getCredentialsByPlatform(@PathVariable String platform) {
        return ResponseEntity.ok(credentialService.getCredentialsByPlatform(platform));
    }

    /**
     * 添加平台凭证
     */
    @PostMapping("/credentials")
    public ResponseEntity<PlatformCredentialDTO> addCredential(@Valid @RequestBody PlatformCredentialDTO dto) {
        return ResponseEntity.ok(credentialService.addCredential(dto));
    }

    /**
     * 更新平台凭证
     */
    @PutMapping("/credentials/{id}")
    public ResponseEntity<PlatformCredentialDTO> updateCredential(@PathVariable String id, @RequestBody PlatformCredentialDTO dto) {
        return ResponseEntity.ok(credentialService.updateCredential(id, dto));
    }

    /**
     * 删除平台凭证
     */
    @DeleteMapping("/credentials/{id}")
    public ResponseEntity<Void> deleteCredential(@PathVariable String id) {
        credentialService.deleteCredential(id);
        return ResponseEntity.ok().build();
    }
}