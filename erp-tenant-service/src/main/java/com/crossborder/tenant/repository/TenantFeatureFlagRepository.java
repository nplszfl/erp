package com.crossborder.tenant.repository;

import com.crossborder.tenant.entity.TenantFeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 租户功能开关仓库
 */
@Repository
public interface TenantFeatureFlagRepository extends JpaRepository<TenantFeatureFlag, String> {

    /**
     * 查询租户的所有功能开关
     */
    List<TenantFeatureFlag> findByTenantId(String tenantId);

    /**
     * 查询租户指定功能开关
     */
    Optional<TenantFeatureFlag> findByTenantIdAndFeatureKey(String tenantId, String featureKey);

    /**
     * 查询租户已启用的功能开关
     */
    List<TenantFeatureFlag> findByTenantIdAndEnabledTrue(String tenantId);

    /**
     * 检查功能开关是否存在
     */
    boolean existsByTenantIdAndFeatureKey(String tenantId, String featureKey);
}