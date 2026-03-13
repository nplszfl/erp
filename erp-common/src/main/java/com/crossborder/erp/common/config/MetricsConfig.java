package com.crossborder.erp.common.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metrics配置
 */
@Configuration
public class MetricsConfig {

    /**
     * 订单创建计数器
     */
    @Bean
    public Counter orderCreateCounter(MeterRegistry registry) {
        return Counter.builder("order.create.count")
                .description("订单创建总数")
                .register(registry);
    }

    /**
     * 订单同步计数器
     */
    @Bean
    public Counter orderSyncCounter(MeterRegistry registry) {
        return Counter.builder("order.sync.count")
                .description("订单同步总数")
                .register(registry);
    }

    /**
     * 当前订单状态分布
     */
    @Bean
    public Gauge orderStatusGauge(MeterRegistry registry) {
        return Gauge.builder("order.status", this::getOrderStatusCount)
                .description("各状态订单数量")
                .tag("status", "", "")
                .register(registry);
    }

    /**
     * 平台API调用次数
     */
    @Bean
    public Counter platformApiCallCounter(MeterRegistry registry) {
        return Counter("platform.api.call.count",
                "平台API调用总次数",
                registry);
    }

    /**
     * 平台API调用失败次数
     */
    @Bean
    public Counter platformApiErrorCounter(MeterRegistry registry) {
        return Counter("platform.api.error.count",
                "平台API调用失败次数",
                registry);
    }

    /**
     * 获取各状态订单数量（示例方法）
     */
    private double getOrderStatusCount() {
        // TODO: 从数据库查询各状态订单数量
        return 0;
    }
}
