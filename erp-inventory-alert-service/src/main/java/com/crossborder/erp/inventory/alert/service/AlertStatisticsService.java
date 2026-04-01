package com.crossborder.erp.inventory.alert.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crossborder.erp.inventory.alert.entity.InventoryAlert;
import com.crossborder.erp.inventory.alert.entity.InventoryAlertStatistics;
import com.crossborder.erp.inventory.alert.mapper.InventoryAlertMapper;
import com.crossborder.erp.inventory.alert.mapper.InventoryAlertStatisticsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 预警统计服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertStatisticsService extends ServiceImpl<InventoryAlertStatisticsMapper, InventoryAlertStatistics> {

    private final InventoryAlertMapper inventoryAlertMapper;

    /**
     * 获取实时统计
     */
    public Map<String, Object> getRealTimeStatistics(String warehouseId) {
        Map<String, Object> result = new HashMap<>();
        
        LambdaQueryWrapper<InventoryAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryAlert::getStatus, "PENDING");
        
        if (StringUtils.hasText(warehouseId)) {
            wrapper.eq(InventoryAlert::getWarehouseId, warehouseId);
        }
        
        List<InventoryAlert> pendingAlerts = inventoryAlertMapper.selectList(wrapper);
        
        result.put("pendingCount", pendingAlerts.size());
        result.put("lowStockCount", pendingAlerts.stream().filter(a -> "LOW".equals(a.getAlertType())).count());
        result.put("outOfStockCount", pendingAlerts.stream().filter(a -> "OUT".equals(a.getAlertType())).count());
        result.put("safeStockWarningCount", pendingAlerts.stream().filter(a -> "SAFE".equals(a.getAlertType())).count());
        result.put("expiredCount", pendingAlerts.stream().filter(a -> "EXPIRED".equals(a.getAlertType())).count());
        
        return result;
    }

    /**
     * 获取趋势数据
     */
    public List<Map<String, Object>> getTrendData(int days, String warehouseId) {
        List<Map<String, Object>> trendList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            
            LambdaQueryWrapper<InventoryAlert> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(InventoryAlert::getCreateTime, startOfDay)
                   .lt(InventoryAlert::getCreateTime, endOfDay);
            
            if (StringUtils.hasText(warehouseId)) {
                wrapper.eq(InventoryAlert::getWarehouseId, warehouseId);
            }
            
            List<InventoryAlert> alerts = inventoryAlertMapper.selectList(wrapper);
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("totalCount", alerts.size());
            dayData.put("pendingCount", alerts.stream().filter(a -> "PENDING".equals(a.getStatus())).count());
            dayData.put("handledCount", alerts.stream().filter(a -> "HANDLED".equals(a.getStatus())).count());
            dayData.put("ignoredCount", alerts.stream().filter(a -> "IGNORED".equals(a.getStatus())).count());
            
            trendList.add(dayData);
        }
        
        return trendList;
    }

    /**
     * 获取预警类型分布
     */
    public Map<String, Integer> getAlertTypeDistribution(String warehouseId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<InventoryAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(InventoryAlert::getCreateTime, startTime)
               .lt(InventoryAlert::getCreateTime, endTime);
        
        if (StringUtils.hasText(warehouseId)) {
            wrapper.eq(InventoryAlert::getWarehouseId, warehouseId);
        }
        
        List<InventoryAlert> alerts = inventoryAlertMapper.selectList(wrapper);
        
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("LOW", (int) alerts.stream().filter(a -> "LOW".equals(a.getAlertType())).count());
        distribution.put("OUT", (int) alerts.stream().filter(a -> "OUT".equals(a.getAlertType())).count());
        distribution.put("SAFE", (int) alerts.stream().filter(a -> "SAFE".equals(a.getAlertType())).count());
        distribution.put("EXPIRED", (int) alerts.stream().filter(a -> "EXPIRED".equals(a.getAlertType())).count());
        
        return distribution;
    }

    /**
     * 获取处理效率
     */
    public Map<String, Object> getProcessingEfficiency(String warehouseId, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        LocalDateTime endTime = LocalDateTime.now();
        
        LambdaQueryWrapper<InventoryAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(InventoryAlert::getCreateTime, startTime)
               .lt(InventoryAlert::getCreateTime, endTime);
        
        if (StringUtils.hasText(warehouseId)) {
            wrapper.eq(InventoryAlert::getWarehouseId, warehouseId);
        }
        
        List<InventoryAlert> alerts = inventoryAlertMapper.selectList(wrapper);
        
        int totalAlertCount = alerts.size();
        int pendingCount = (int) alerts.stream().filter(a -> "PENDING".equals(a.getStatus())).count();
        int handledCount = (int) alerts.stream().filter(a -> "HANDLED".equals(a.getStatus())).count();
        int ignoredCount = (int) alerts.stream().filter(a -> "IGNORED".equals(a.getStatus())).count();
        
        double handleRate = totalAlertCount > 0 ? (double) handledCount / totalAlertCount * 100 : 0;
        double ignoreRate = totalAlertCount > 0 ? (double) ignoredCount / totalAlertCount * 100 : 0;
        
        // 计算平均处理时间（简化计算）
        double avgHandleTime = alerts.stream()
                .filter(a -> "HANDLED".equals(a.getStatus()) && a.getCreateTime() != null)
                .mapToLong(a -> ChronoUnit.MINUTES.between(a.getCreateTime(), LocalDateTime.now()))
                .average()
                .orElse(0);
        
        Map<String, Object> efficiency = new HashMap<>();
        efficiency.put("totalCount", totalAlertCount);
        efficiency.put("pendingCount", pendingCount);
        efficiency.put("handledCount", handledCount);
        efficiency.put("ignoredCount", ignoredCount);
        efficiency.put("handleRate", Math.round(handleRate * 100) / 100.0);
        efficiency.put("ignoreRate", Math.round(ignoreRate * 100) / 100.0);
        efficiency.put("avgHandleTimeMinutes", Math.round(avgHandleTime * 100) / 100.0);
        
        return efficiency;
    }

    /**
     * 生成日报
     */
    @Transactional
    public void generateDailyReport(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        LambdaQueryWrapper<InventoryAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(InventoryAlert::getCreateTime, startOfDay)
               .lt(InventoryAlert::getCreateTime, endOfDay);
        
        List<InventoryAlert> alerts = inventoryAlertMapper.selectList(wrapper);
        
        InventoryAlertStatistics statistics = new InventoryAlertStatistics();
        statistics.setStatisticsDate(date);
        statistics.setTotalAlertCount(alerts.size());
        statistics.setPendingCount((int) alerts.stream().filter(a -> "PENDING".equals(a.getStatus())).count());
        statistics.setHandledCount((int) alerts.stream().filter(a -> "HANDLED".equals(a.getStatus())).count());
        statistics.setIgnoredCount((int) alerts.stream().filter(a -> "IGNORED".equals(a.getStatus())).count());
        statistics.setLowStockCount((int) alerts.stream().filter(a -> "LOW".equals(a.getAlertType())).count());
        statistics.setOutOfStockCount((int) alerts.stream().filter(a -> "OUT".equals(a.getAlertType())).count());
        statistics.setSafeStockWarningCount((int) alerts.stream().filter(a -> "SAFE".equals(a.getAlertType())).count());
        statistics.setCreateTime(LocalDateTime.now());
        
        save(statistics);
        log.info("生成预警日报: {}, 总预警数: {}", date, alerts.size());
    }
}