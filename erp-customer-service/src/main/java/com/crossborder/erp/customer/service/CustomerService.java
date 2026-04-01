package com.crossborder.erp.customer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crossborder.erp.customer.entity.Customer;
import com.crossborder.erp.customer.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户服务 - 客户管理与分析
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService extends ServiceImpl<CustomerMapper, Customer> {

    /**
     * 创建客户
     */
    public Long createCustomer(Customer customer) {
        if (customer.getStatus() == null) {
            customer.setStatus("ACTIVE");
        }
        if (customer.getLevel() == null) {
            customer.setLevel("NORMAL");
        }
        if (customer.getTotalOrders() == null) {
            customer.setTotalOrders(0);
        }
        if (customer.getTotalSpent() == null) {
            customer.setTotalSpent(BigDecimal.ZERO);
        }
        
        save(customer);
        log.info("创建客户成功: {}", customer.getName());
        return customer.getId();
    }

    /**
     * 根据平台和客户ID获取或创建客户
     */
    public Customer getOrCreateByPlatform(String platform, String platformCustomerId, String name, String email) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getPlatform, platform)
               .eq(Customer::getPlatformCustomerId, platformCustomerId);
        
        Customer customer = getOne(wrapper);
        if (customer == null) {
            customer = new Customer();
            customer.setPlatform(platform);
            customer.setPlatformCustomerId(platformCustomerId);
            customer.setName(name);
            customer.setEmail(email);
            customer.setStatus("ACTIVE");
            customer.setLevel("NEW");
            customer.setTotalOrders(0);
            customer.setTotalSpent(BigDecimal.ZERO);
            save(customer);
        }
        return customer;
    }

    /**
     * 客户消费更新（下单时调用）
     */
    public void updatePurchaseStats(Long customerId, BigDecimal orderAmount) {
        Customer customer = getById(customerId);
        if (customer != null) {
            customer.setTotalOrders(customer.getTotalOrders() + 1);
            customer.setTotalSpent(customer.getTotalSpent().add(orderAmount));
            // 计算平均订单金额
            if (customer.getTotalOrders() > 0) {
                customer.setAvgOrderValue(
                    customer.getTotalSpent().divide(
                        BigDecimal.valueOf(customer.getTotalOrders()), 2, BigDecimal.ROUND_HALF_UP
                    )
                );
            }
            customer.setLastOrderAt(LocalDateTime.now());
            
            // 自动升级客户等级
            if (customer.getTotalSpent().compareTo(new BigDecimal("10000")) >= 0) {
                customer.setLevel("VIP");
            } else if (customer.getTotalSpent().compareTo(new BigDecimal("3000")) >= 0) {
                customer.setLevel("NORMAL");
            }
            
            updateById(customer);
            log.info("客户消费统计更新: customerId={}, totalOrders={}, totalSpent={}", 
                customerId, customer.getTotalOrders(), customer.getTotalSpent());
        }
    }

    /**
     * 分页查询客户
     */
    public IPage<Customer> pageCustomers(Page<Customer> page, String keyword, String platform, 
                                          String level, String country) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Customer::getName, keyword)
                             .or().like(Customer::getEmail, keyword)
                             .or().like(Customer::getPhone, keyword));
        }
        if (StringUtils.hasText(platform)) {
            wrapper.eq(Customer::getPlatform, platform);
        }
        if (StringUtils.hasText(level)) {
            wrapper.eq(Customer::getLevel, level);
        }
        if (StringUtils.hasText(country)) {
            wrapper.eq(Customer::getCountry, country);
        }
        
        wrapper.orderByDesc(Customer::getTotalSpent, Customer::getTotalOrders);
        
        return page(page, wrapper);
    }

    /**
     * 获取客户统计数据
     */
    public CustomerStats getStats() {
        List<Customer> all = list();
        
        int totalCustomers = all.size();
        int vipCount = (int) all.stream().filter(c -> "VIP".equals(c.getLevel())).count();
        int activeCount = (int) all.stream().filter(c -> "ACTIVE".equals(c.getStatus())).count();
        
        BigDecimal totalRevenue = all.stream()
            .map(Customer::getTotalSpent)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal avgSpent = totalCustomers > 0 
            ? totalRevenue.divide(BigDecimal.valueOf(totalCustomers), 2, BigDecimal.ROUND_HALF_UP)
            : BigDecimal.ZERO;
        
        return new CustomerStats(totalCustomers, vipCount, activeCount, totalRevenue, avgSpent);
    }

    /**
     * 客户统计数据
     */
    public record CustomerStats(int totalCustomers, int vipCount, int activeCount, 
                                 BigDecimal totalRevenue, BigDecimal avgSpent) {}
}