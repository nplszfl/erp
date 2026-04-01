package com.crossborder.tenant.service;

import com.crossborder.tenant.config.TenantContext;
import com.crossborder.tenant.dto.dashboard.DashboardDTO;
import com.crossborder.tenant.dto.dashboard.DailyUsageDTO;
import com.crossborder.tenant.dto.dashboard.UsageStatisticsDTO;
import com.crossborder.tenant.entity.*;
import com.crossborder.tenant.entity.billing.Bill;
import com.crossborder.tenant.entity.usage.UsageRecord;
import com.crossborder.tenant.repository.*;
import com.crossborder.tenant.repository.billing.BillRepository;
import com.crossborder.tenant.repository.usage.UsageRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 租户仪表盘服务 - 提供租户端数据概览
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TenantRepository tenantRepository;
    private final TenantUserRepository userRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final BillRepository billRepository;

    private static final long GB = 1024L * 1024 * 1024;

    /**
     * 获取租户仪表盘数据
     */
    public DashboardDTO getDashboard() {
        String tenantId = TenantContext.getTenant();
        
        // 获取租户信息
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("租户不存在"));
        
        TenantPlan plan = tenant.getPlan();
        
        // 获取当前月份使用量
        YearMonth currentPeriod = YearMonth.now();
        UsageRecord usage = usageRecordRepository
                .findByTenantIdAndBillingPeriod(tenantId, currentPeriod)
                .orElse(UsageRecord.builder()
                        .tenantId(tenantId)
                        .billingPeriod(currentPeriod)
                        .build());
        
        // 获取本月账单
        Bill currentBill = billRepository
                .findByTenantIdAndBillingPeriod(tenantId, currentPeriod)
                .orElse(null);
        
        // 获取活跃用户数
        List<TenantUser> users = userRepository.findByTenantId(tenantId);
        int activeUsers = (int) users.stream()
                .filter(u -> u.getLastLoginAt() != null 
                        && u.getLastLoginAt().isAfter(LocalDateTime.now().minusDays(30)))
                .count();
        
        // 计算配额和百分比
        int apiCallsLimit = plan.getOrderLimit();
        long storageLimit = 10L * GB;
        int orderLimit = plan.getOrderLimit();
        int platformLimit = plan.getPlatformLimit();
        long aiCallsLimit = plan.getAiQuota() * 1000L;
        
        Double apiCallsPercent = calculatePercent(usage.getApiCalls(), apiCallsLimit);
        Double storagePercent = calculatePercent(usage.getStorageBytes(), storageLimit);
        Double orderPercent = calculatePercent((long) usage.getOrderCount(), orderLimit);
        Double platformPercent = calculatePercent((long) usage.getPlatformCount(), platformLimit);
        Double aiCallsPercent = calculatePercent(usage.getAiCalls(), aiCallsLimit);
        
        // 计算到期天数
        Integer daysRemaining = null;
        Boolean expiringSoon = false;
        if (tenant.getExpiresAt() != null) {
            daysRemaining = (int) ChronoUnit.DAYS.between(LocalDateTime.now(), tenant.getExpiresAt());
            expiringSoon = daysRemaining <= 7 && daysRemaining > 0;
        }
        
        return DashboardDTO.builder()
                .tenantId(tenant.getId())
                .tenantName(tenant.getName())
                .plan(plan.name())
                .status(tenant.getStatus().name())
                .apiCalls(usage.getApiCalls())
                .storageBytes(usage.getStorageBytes())
                .orderCount(usage.getOrderCount())
                .platformCount(usage.getPlatformCount())
                .aiCalls(usage.getAiCalls())
                .apiCallsLimit(apiCallsLimit)
                .storageLimit(storageLimit)
                .orderLimit(orderLimit)
                .platformLimit(platformLimit)
                .aiCallsLimit(aiCallsLimit)
                .apiCallsPercent(apiCallsPercent)
                .storagePercent(storagePercent)
                .orderPercent(orderPercent)
                .platformPercent(platformPercent)
                .aiCallsPercent(aiCallsPercent)
                .currentBill(currentBill != null ? currentBill.getTotalAmount().doubleValue() : 0.0)
                .billStatus(currentBill != null ? currentBill.getStatus().name() : "NO_BILL")
                .activeUsers(activeUsers)
                .lastActivity(users.stream()
                        .filter(u -> u.getLastLoginAt() != null)
                        .map(TenantUser::getLastLoginAt)
                        .max(LocalDateTime::compareTo)
                        .orElse(null))
                .expiresAt(tenant.getExpiresAt())
                .daysRemaining(daysRemaining)
                .expiringSoon(expiringSoon)
                .build();
    }

    /**
     * 获取使用量趋势（最近7天）
     */
    public List<DailyUsageDTO> getUsageTrend() {
        String tenantId = TenantContext.getTenant();
        LocalDate today = LocalDate.now();
        
        // 生成7天趋势（实际项目中从数据库查询）
        return IntStream.rangeClosed(1, 7)
                .mapToObj(i -> {
                    LocalDate date = today.minusDays(7 - i);
                    // 模拟数据，实际应从日志表查询
                    long apiCalls = (long) (Math.random() * 1000);
                    int orders = (int) (Math.random() * 50);
                    long storage = (long) (Math.random() * 100 * 1024 * 1024);
                    
                    return DailyUsageDTO.builder()
                            .date(date)
                            .apiCalls(apiCalls)
                            .orders(orders)
                            .storageBytes(storage)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取配额使用概览
     */
    public Map<String, Object> getQuotaOverview() {
        String tenantId = TenantContext.getTenant();
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("租户不存在"));
        
        TenantPlan plan = tenant.getPlan();
        YearMonth currentPeriod = YearMonth.now();
        
        UsageRecord usage = usageRecordRepository
                .findByTenantIdAndBillingPeriod(tenantId, currentPeriod)
                .orElse(UsageRecord.builder().build());
        
        Map<String, Object> overview = new HashMap<>();
        
        // API调用配额
        overview.put("apiCalls", Map.of(
                "used", usage.getApiCalls(),
                "limit", plan.getOrderLimit(),
                "percent", calculatePercent(usage.getApiCalls(), plan.getOrderLimit())
        ));
        
        // 存储配额
        overview.put("storage", Map.of(
                "used", usage.getStorageBytes(),
                "limit", 10L * GB,
                "percent", calculatePercent(usage.getStorageBytes(), 10L * GB)
        ));
        
        // 订单配额
        overview.put("orders", Map.of(
                "used", usage.getOrderCount(),
                "limit", plan.getOrderLimit(),
                "percent", calculatePercent((long) usage.getOrderCount(), plan.getOrderLimit())
        ));
        
        // 平台配额
        overview.put("platforms", Map.of(
                "used", usage.getPlatformCount(),
                "limit", plan.getPlatformLimit(),
                "percent", calculatePercent((long) usage.getPlatformCount(), plan.getPlatformLimit())
        ));
        
        // AI调用配额
        overview.put("aiCalls", Map.of(
                "used", usage.getAiCalls(),
                "limit", plan.getAiQuota() * 1000L,
                "percent", calculatePercent(usage.getAiCalls(), plan.getAiQuota() * 1000L)
        ));
        
        return overview;
    }

    private Double calculatePercent(long used, long limit) {
        if (limit <= 0) return 0.0;
        double percent = (double) used / limit * 100;
        return Math.min(percent, 100.0);
    }
}