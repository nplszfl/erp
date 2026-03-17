package com.crossborder.pricing.service.impl;

import com.crossborder.pricing.entity.PriceHistory;
import com.crossborder.pricing.service.PriceHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 价格历史服务实现
 */
@Slf4j
@Service
public class PriceHistoryServiceImpl implements PriceHistoryService {

    // 模拟数据库存储
    private final List<PriceHistory> priceHistoryList = new CopyOnWriteArrayList<>();

    @Override
    public void recordPriceChange(PriceHistory history) {
        log.info("记录价格变更 - 产品ID: {}, 旧价格: {}, 新价格: {}",
                history.getProductId(), history.getOldPrice(), history.getNewPrice());

        history.setCreateTime(LocalDateTime.now());

        // TODO: 保存到数据库
        priceHistoryList.add(history);

        log.info("价格变更记录成功");
    }

    @Override
    public List<PriceHistory> getPriceHistory(Long productId) {
        log.info("获取产品价格历史 - 产品ID: {}", productId);

        // TODO: 从数据库查询
        return priceHistoryList.stream()
                .filter(h -> h.getProductId().equals(productId))
                .sorted(Comparator.comparing(PriceHistory::getCreateTime).reversed())
                .toList();
    }

    @Override
    public List<PriceHistory> getPriceHistory(Long productId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("获取指定时间范围的价格历史 - 产品ID: {}, 开始时间: {}, 结束时间: {}",
                productId, startTime, endTime);

        // TODO: 从数据库查询
        return priceHistoryList.stream()
                .filter(h -> h.getProductId().equals(productId))
                .filter(h -> !h.getCreateTime().isBefore(startTime))
                .filter(h -> !h.getCreateTime().isAfter(endTime))
                .sorted(Comparator.comparing(PriceHistory::getCreateTime).reversed())
                .toList();
    }

    @Override
    public List<PriceHistory> getRecentPriceChanges(Long productId, int limit) {
        log.info("获取最近N次价格变更 - 产品ID: {}, 数量: {}", productId, limit);

        return priceHistoryList.stream()
                .filter(h -> h.getProductId().equals(productId))
                .sorted(Comparator.comparing(PriceHistory::getCreateTime).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public PriceTrendAnalysis getPriceTrendAnalysis(Long productId, int days) {
        log.info("获取价格趋势分析 - 产品ID: {}, 天数: {}", productId, days);

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);

        List<PriceHistory> histories = getPriceHistory(productId, startDate, endDate);

        if (histories.isEmpty()) {
            log.warn("没有找到价格历史数据");
            return null;
        }

        // 排序按时间升序
        histories.sort(Comparator.comparing(PriceHistory::getCreateTime));

        BigDecimal startingPrice = histories.get(0).getOldPrice();
        BigDecimal endingPrice = histories.get(histories.size() - 1).getNewPrice();

        BigDecimal priceChangeAmount = endingPrice.subtract(startingPrice);
        BigDecimal priceChangePercent = calculatePriceChangePercent(startingPrice, endingPrice);

        PriceTrendAnalysis analysis = new PriceTrendAnalysis();
        analysis.setProductId(productId);
        analysis.setStartingPrice(startingPrice);
        analysis.setEndingPrice(endingPrice);
        analysis.setPriceChangeAmount(priceChangeAmount);
        analysis.setPriceChangePercent(priceChangePercent);
        analysis.setTotalChanges((long) histories.size());
        analysis.setStartDate(startDate);
        analysis.setEndDate(endDate);

        log.info("价格趋势分析 - 变动金额: {}, 变动百分比: {}, 总次数: {}",
                priceChangeAmount, priceChangePercent, histories.size());

        return analysis;
    }

    // ========== 私有方法 ==========

    /**
     * 计算价格变动百分比
     */
    private BigDecimal calculatePriceChangePercent(BigDecimal oldPrice, BigDecimal newPrice) {
        if (oldPrice == null || oldPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return newPrice.subtract(oldPrice)
                .divide(oldPrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
