package com.crossborder.tenant.service;

import com.crossborder.tenant.config.TenantContext;
import com.crossborder.tenant.dto.platform.PlatformCredentialDTO;
import com.crossborder.tenant.entity.platform.CredentialStatus;
import com.crossborder.tenant.entity.platform.PlatformCredential;
import com.crossborder.tenant.repository.PlatformCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 平台凭证服务 - 管理租户的电商平台店铺连接
 */
@Service
@RequiredArgsConstructor
public class PlatformCredentialService {

    private final PlatformCredentialRepository credentialRepository;

    /**
     * 获取当前租户的所有平台凭证
     */
    public List<PlatformCredentialDTO> getCredentials() {
        String tenantId = TenantContext.getTenant();
        return credentialRepository.findByTenantId(tenantId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取指定平台的凭证
     */
    public List<PlatformCredentialDTO> getCredentialsByPlatform(String platform) {
        String tenantId = TenantContext.getTenant();
        return credentialRepository.findByTenantIdAndPlatform(tenantId, platform).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 添加平台凭证
     */
    public PlatformCredentialDTO addCredential(PlatformCredentialDTO dto) {
        String tenantId = TenantContext.getTenant();
        
        PlatformCredential credential = PlatformCredential.builder()
                .tenantId(tenantId)
                .platform(dto.getPlatform())
                .shopName(dto.getShopName())
                .shopId(dto.getShopId())
                .clientId(dto.getClientId())
                .clientSecret(dto.getClientSecret())
                .developerId(dto.getDeveloperId())
                .refreshToken(dto.getRefreshToken())
                .marketplace(dto.getMarketplace())
                .status(CredentialStatus.ACTIVE)
                .build();
        
        credential = credentialRepository.save(credential);
        return toDTO(credential);
    }

    /**
     * 更新凭证
     */
    public PlatformCredentialDTO updateCredential(String id, PlatformCredentialDTO dto) {
        String tenantId = TenantContext.getTenant();
        
        PlatformCredential credential = credentialRepository.findById(id)
                .filter(c -> c.getTenantId().equals(tenantId))
                .orElseThrow(() -> new RuntimeException("凭证不存在"));
        
        if (dto.getShopName() != null) credential.setShopName(dto.getShopName());
        if (dto.getClientId() != null) credential.setClientId(dto.getClientId());
        if (dto.getClientSecret() != null) credential.setClientSecret(dto.getClientSecret());
        if (dto.getRefreshToken() != null) credential.setRefreshToken(dto.getRefreshToken());
        
        credential = credentialRepository.save(credential);
        return toDTO(credential);
    }

    /**
     * 删除凭证
     */
    public void deleteCredential(String id) {
        String tenantId = TenantContext.getTenant();
        PlatformCredential credential = credentialRepository.findById(id)
                .filter(c -> c.getTenantId().equals(tenantId))
                .orElseThrow(() -> new RuntimeException("凭证不存在"));
        credentialRepository.delete(credential);
    }

    private PlatformCredentialDTO toDTO(PlatformCredential credential) {
        return PlatformCredentialDTO.builder()
                .id(credential.getId())
                .tenantId(credential.getTenantId())
                .platform(credential.getPlatform())
                .shopName(credential.getShopName())
                .shopId(credential.getShopId())
                .clientId(credential.getClientId())
                .developerId(credential.getDeveloperId())
                .refreshToken(credential.getRefreshToken())
                .marketplace(credential.getMarketplace())
                .status(credential.getStatus())
                .accessToken(credential.getAccessToken())
                .tokenExpiresAt(credential.getTokenExpiresAt())
                .lastSyncAt(credential.getLastSyncAt())
                .createdAt(credential.getCreatedAt())
                .build();
    }
}