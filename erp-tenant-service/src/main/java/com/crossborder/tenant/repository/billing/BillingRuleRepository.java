package com.crossborder.tenant.repository.billing;

import com.crossborder.tenant.entity.billing.BillingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 计费规则仓库
 */
@Repository
public interface BillingRuleRepository extends JpaRepository<BillingRule, String> {

    /**
     * 根据资源类型查询计费规则
     */
    List<BillingRule> findByResourceTypeAndActiveTrue(BillingRule.ResourceType resourceType);

    /**
     * 查询所有活跃的计费规则
     */
    List<BillingRule> findByActiveTrue();

    /**
     * 根据名称查询规则
     */
    Optional<BillingRule> findByName(String name);
}