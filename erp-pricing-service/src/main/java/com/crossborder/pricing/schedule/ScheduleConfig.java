package com.crossborder.pricing.schedule;

import com.crossborder.pricing.service.CompetitorScrapeService;
import com.crossborder.pricing.service.PricingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * 定时任务配置
 */
@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfig {

    private final CompetitorScrapeService competitorScrapeService;
    private final PricingService pricingService;

    public ScheduleConfig(CompetitorScrapeService competitorScrapeService, PricingService pricingService) {
        this.competitorScrapeService = competitorScrapeService;
        this.pricingService = pricingService;
    }

    /**
     * 竞品数据抓取定时任务
     * 每小时执行一次
     */
    @Scheduled(fixedRate = 60 * 60 * 1000) // 60分钟
    public void scrapeCompetitorProductsSchedule() {
        log.info("🕐 定时任务：开始抓取竞品数据");

        try {
            // TODO: 从数据库获取所有需要监控的产品
            List<Long> productIds = getMonitoredProducts();

            if (!productIds.isEmpty()) {
                competitorScrapeService.batchScrape(productIds);
                log.info("✅ 竞品数据抓取完成 - 产品数量: {}", productIds.size());
            } else {
                log.debug("没有需要监控的产品");
            }

        } catch (Exception e) {
            log.error("❌ 竞品数据抓取失败", e);
        }
    }

    /**
     * 动态定价定时任务
     * 每30分钟执行一次
     */
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30分钟
    public void dynamicPricingSchedule() {
        log.info("🕐 定时任务：开始动态定价调整");

        try {
            // TODO: 从数据库获取所有需要定价的产品
            List<Long> productIds = getMonitoredProducts();

            int adjustedCount = 0;
            for (Long productId : productIds) {
                try {
                    pricingService.adjustPriceByCompetitors(productId);
                    adjustedCount++;
                } catch (Exception e) {
                    log.error("产品 {} 价格调整失败", productId, e);
                }
            }

            log.info("✅ 动态定价完成 - 调整数量: {}", adjustedCount);

        } catch (Exception e) {
            log.error("❌ 动态定价失败", e);
        }
    }

    /**
     * 获取需要监控的产品（模拟）
     */
    private List<Long> getMonitoredProducts() {
        // TODO: 从数据库查询
        return List.of(1L, 2L, 3L, 4L, 5L);
    }
}
