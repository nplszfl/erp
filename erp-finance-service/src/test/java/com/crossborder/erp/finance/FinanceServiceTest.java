package com.crossborder.erp.finance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.finance.entity.FinanceRecord;
import com.crossborder.erp.finance.entity.FinanceAccount;
import com.crossborder.erp.finance.entity.FinanceCategory;
import com.crossborder.erp.finance.enums.FinanceType;
import com.crossborder.erp.finance.service.FinanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 财务服务单元测试
 */
@SpringBootTest
public class FinanceServiceTest {

    @Autowired
    private FinanceService financeService;

    @Test
    public void testCreateAccount() {
        FinanceAccount account = new FinanceAccount();
        account.setName("测试账户");
        account.setCode("TEST001");
        account.setAccountType("BANK"); // 银行账户
        account.setRemark("单元测试");

        Long accountId = financeService.createAccount(account);
        assertNotNull(accountId);
        assertTrue(accountId > 0);

        var accounts = financeService.listAccounts();
        assertFalse(accounts.isEmpty());
    }

    @Test
    public void testRecordIncome() {
        FinanceRecord record = new FinanceRecord();
        record.setAccountId(1L);
        record.setCategoryId(1L);
        record.setType(FinanceType.INCOME);
        record.setAmount(new BigDecimal("1000.00"));
        record.setRemark("测试收入");

        Long recordId = financeService.recordFinance(record);
        assertNotNull(recordId);
        assertTrue(recordId > 0);
    }

    @Test
    public void testRecordExpense() {
        FinanceRecord record = new FinanceRecord();
        record.setAccountId(1L);
        record.setCategoryId(2L);
        record.setType(FinanceType.EXPENSE);
        record.setAmount(new BigDecimal("500.00"));
        record.setRemark("测试支出");

        Long recordId = financeService.recordFinance(record);
        assertNotNull(recordId);
        assertTrue(recordId > 0);
    }

    @Test
    public void testListRecords() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();

        Page<FinanceRecord> page = financeService.listRecords(
                null, null, startTime, endTime, 1, 10
        );

        assertNotNull(page);
        assertNotNull(page.getRecords());
    }

    @Test
    public void testCalculateTotalIncome() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();

        BigDecimal totalIncome = financeService.calculateTotalIncome(startTime, endTime);
        assertNotNull(totalIncome);
        assertTrue(totalIncome.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    public void testCalculateTotalExpense() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();

        BigDecimal totalExpense = financeService.calculateTotalExpense(startTime, endTime);
        assertNotNull(totalExpense);
        assertTrue(totalExpense.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    public void testCalculateProfit() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();

        BigDecimal profit = financeService.calculateProfit(startTime, endTime);
        assertNotNull(profit);
    }

    @Test
    public void testGetDailyStatistics() {
        LocalDateTime date = LocalDateTime.now();

        var statistics = financeService.getDailyStatistics(date);
        assertNotNull(statistics);
        assertNotNull(statistics.getIncome());
        assertNotNull(statistics.getExpense());
        assertNotNull(statistics.getProfit());
    }

    @Test
    public void testUpdateAccountBalance() {
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("200.00");

        financeService.updateAccountBalance(
                accountId, amount,
                com.crossborder.erp.finance.enums.OperationType.INCREASE
        );

        var accounts = financeService.listAccounts();
        var account = accounts.stream()
                .filter(a -> a.getId().equals(accountId))
                .findFirst();
        
        assertTrue(account.isPresent());
        assertTrue(account.get().getBalance().compareTo(BigDecimal.ZERO) > 0);
    }
}
