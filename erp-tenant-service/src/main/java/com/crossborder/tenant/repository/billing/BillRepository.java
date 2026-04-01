package com.crossborder.tenant.repository.billing;

import com.crossborder.tenant.entity.billing.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * 账单仓库
 */
@Repository
public interface BillRepository extends JpaRepository<Bill, String> {

    /**
     * 查询租户所有账单
     */
    List<Bill> findByTenantIdOrderByBillingPeriodDesc(String tenantId);

    /**
     * 查询租户指定月份的账单
     */
    Optional<Bill> findByTenantIdAndBillingPeriod(String tenantId, YearMonth billingPeriod);

    /**
     * 查询租户待支付账单
     */
    List<Bill> findByTenantIdAndStatusOrderByDueDateAsc(String tenantId, Bill.BillStatus status);

    /**
     * 查询租户最近账单
     */
    Optional<Bill> findTopByTenantIdOrderByBillingPeriodDesc(String tenantId);
}