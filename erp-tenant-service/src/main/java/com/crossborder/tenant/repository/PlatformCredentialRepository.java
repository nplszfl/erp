package com.crossborder.tenant.repository;

import com.crossborder.tenant.entity.platform.CredentialStatus;
import com.crossborder.tenant.entity.platform.PlatformCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlatformCredentialRepository extends JpaRepository<PlatformCredential, String> {
    List<PlatformCredential> findByTenantId(String tenantId);
    List<PlatformCredential> findByTenantIdAndPlatform(String tenantId, String platform);
    List<PlatformCredential> findByTenantIdAndStatus(String tenantId, CredentialStatus status);
}