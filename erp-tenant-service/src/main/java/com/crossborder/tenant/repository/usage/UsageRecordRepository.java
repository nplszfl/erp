package com.crossborder.tenant.repository.usage;

import com.crossborder.tenant.entity.usage.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.Optional;

/**
 * 使用量记录仓库
 */
@Repository
public interface UsageRecordRepository extends JpaRepository<UsageRecord, String> {

    /**
     * 查询指定租户指定月份的使用量
     */
    Optional<UsageRecord> findByTenantIdAndBillingPeriod(String tenantId, YearMonth billingPeriod);

    /**
     * 查询指定租户最近的使用量
     */
    Optional<UsageRecord> findTopByTenantIdOrderByBillingPeriodDesc(String tenantId);
}