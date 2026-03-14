package com.crossborder.erp.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.finance.entity.*;
import com.crossborder.erp.finance.enums.*;
import com.crossborder.erp.finance.mapper.*;
import com.crossborder.erp.finance.service.FinanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 财务服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final FinanceRecordMapperRecordMapper;
    private final FinanceAccountMapper accountMapper;
    private final FinanceCategoryMapper categoryMapper;
    private final FinanceStatisticsMapper statisticsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long recordFinance(FinanceRecord record) {
        record.setRecordNo(generateRecordNo());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        
        // 计算账户余额
        FinanceAccount account = accountMapper.selectById(record.getAccountId());
        BigDecimal oldBalance = account.getBalance();
        
        if (record.getType() == FinanceType.INCOME) {
            BigDecimal newBalance = oldBalance.add(record.getAmount());
            record.setAfterBalance(newBalance);
            account.setBalance(newBalance);
        } else {
            BigDecimal newBalance = oldBalance.subtract(record.getAmount());
            record.setAfterBalance(newBalance);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("账户余额不足");
            }
            account.setBalance(newBalance);
        }
        
        record.setBeforeBalance(oldBalance);
        account.setUpdateTime(LocalDateTime.now());
        
        // 保存财务记录
        recordMapper.insert(record);
        // 更新账户余额
        accountMapper.updateById(account);
        
        log.info("记录财务成功: {} {}", record.getType().getDescription(), record.getAmount());
        return record.getId();
    }

    @Override
    public Page<FinanceRecord> listRecords(Long accountId, FinanceType type,
                                          LocalDateTime startTime, LocalDateTime endTime,
                                          int page, int size) {
        Page<FinanceRecord> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (accountId != null) {
            wrapper.eq(FinanceRecord::getAccountId, accountId);
        }
        if (type != null) {
            wrapper.eq(FinanceRecord::getType, type);
        }
        if (startTime != null) {
            wrapper.ge(FinanceRecord::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(FinanceRecord::getCreateTime, endTime);
        }
        
        wrapper.orderByDesc(FinanceRecord::getCreateTime);
        return recordMapper.selectPage(pageObj, wrapper);
    }

    @Override
    public BigDecimal calculateTotalIncome(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceRecord::getType, FinanceType.INCOME);
        
        if (startTime != null) {
            wrapper.ge(FinanceRecord::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(FinanceRecord::getCreateTime, endTime);
        }
        
        List<FinanceRecord> records = recordMapper.selectList(wrapper);
        return records.stream()
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateTotalExpense(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceRecord::getType, FinanceType.EXPENSE);
        
        if (startTime != null) {
            wrapper.ge(FinanceRecord::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(FinanceRecord::getCreateTime, endTime);
        }
        
        List<FinanceRecord> records = recordMapper.selectList(wrapper);
        return records.stream()
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateProfit(LocalDateTime startTime, LocalDateTime endTime) {
        BigDecimal income = calculateTotalIncome(startTime, endTime);
        BigDecimal expense = calculateTotalExpense(startTime, endTime);
        return income.subtract(expense);
    }

    @Override
    public List<CategoryStatistics> calculateCategoryStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        // 获取所有财务记录
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(FinanceRecord::getCreateTime, startTime, endTime);
        List<FinanceRecord> records = recordMapper.selectList(wrapper);
        
        // 按类别统计
        return records.stream()
                .collect(Collectors.groupingBy(
                        FinanceRecord::getCategoryId,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                FinanceRecord::getAmount,
                                BigDecimal::add
                        )
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    CategoryStatistics stats = new CategoryStatistics();
                    stats.setCategoryId(entry.getKey());
                    FinanceCategory category = categoryMapper.selectById(entry.getKey());
                    stats.setCategoryName(category != null ? category.getName() : "未知");
                    stats.setTotalAmount(entry.getValue());
                    return stats;
                })
                .sorted((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FinanceAccount> listAccounts() {
        return accountMapper.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAccount(FinanceAccount account) {
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());
        accountMapper.insert(account);
        
        log.info("创建账户成功: {}", account.getName());
        return account.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAccountBalance(Long accountId, BigDecimal amount, OperationType operation) {
        FinanceAccount account = accountMapper.selectById(accountId);
        if (account == null) {
            throw new RuntimeException("账户不存在");
        }
        
        if (operation == OperationType.INCREASE) {
            account.setBalance(account.getBalance().add(amount));
        } else {
            account.setBalance(account.getBalance().subtract(amount));
            if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("账户余额不足");
            }
        }
        
        account.setUpdateTime(LocalDateTime.now());
        accountMapper.updateById(account);
        
        log.info("更新账户余额: {} {}", account.getName(), account.getBalance());
    }

    @Override
    public List<FinanceCategory> listCategories(CategoryType type) {
        LambdaQueryWrapper<FinanceCategory> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(FinanceCategory::getType, type);
        }
        return categoryMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(FinanceCategory category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(category);
        
        log.info("创建类别成功: {}", category.getName());
        return category.getId();
    }

    @Override
    public FinanceDailyStatistics getDailyStatistics(LocalDateTime date) {
        LocalDateTime startTime = date.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endTime = date.withHour(23).withMinute(59).withSecond(59);
        
        BigDecimal income = calculateTotalIncome(startTime, endTime);
        BigDecimal expense = calculateTotalExpense(startTime, endTime);
        BigDecimal profit = income.subtract(expense);
        
        FinanceDailyStatistics statistics = new FinanceDailyStatistics();
        statistics.setDate(date);
        statistics.setIncome(income);
        statistics.setExpense(expense);
        statistics.setProfit(profit);
        
        return statistics;
    }

    @Override
    public List<FinanceMonthlyStatistics> getMonthlyStatistics(Integer year, Integer month) {
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endTime = startTime.plusMonths(1).minusSeconds(1);
        
        // 按天统计
        List<FinanceDailyStatistics> dailyStats = new ArrayList<>();
        LocalDateTime current = startTime;
        while (current.isBefore(endTime)) {
            dailyStats.add(getDailyStatistics(current));
            current = current.plusDays(1);
        }
        
        // 汇总月度统计
        FinanceMonthlyStatistics monthlyStats = new FinanceMonthlyStatistics();
        monthlyStats.setYear(year);
        monthlyStats.setMonth(month);
        monthlyStats.setTotalIncome(dailyStats.stream()
                .map(FinanceDailyStatistics::getIncome)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        monthlyStats.setTotalExpense(dailyStats.stream()
                .map(FinanceDailyStatistics::getExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        monthlyStats.setTotalProfit(dailyStats.stream()
                .map(FinanceDailyStatistics::getProfit)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        return Arrays.asList(monthlyStats);
    }

    /**
     * 生成财务记录号
     */
    private String generateRecordNo() {
        return "FIN" + System.currentTimeMillis();
    }
}
