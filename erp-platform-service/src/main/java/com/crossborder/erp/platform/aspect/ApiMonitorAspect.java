package com.crossborder.erp.platform.aspect;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * API监控切面 - 记录API调用耗时和埋点
 * 集成Micrometer进行指标采集，可导出到Prometheus
 */
@Slf4j
@Aspect
@Component
public class ApiMonitorAspect {

    private final MeterRegistry meterRegistry;

    public ApiMonitorAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("@annotation(com.crossborder.erp.platform.annotation.ApiMonitor)")
    public Object monitorApi(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String signature = className + "." + methodName;

        Instant start = Instant.now();
        
        // 获取或创建Timer
        Timer timer = Timer.builder("api.call.duration")
                .tag("method", signature)
                .description("API调用耗时")
                .register(meterRegistry);

        try {
            Object result = joinPoint.proceed();
            long duration = Duration.between(start, Instant.now()).toMillis();

            // 记录成功指标
            recordSuccessMetrics(signature, duration);

            log.info("API调用成功: {} 耗时: {}ms", signature, duration);
            return result;

        } catch (Exception e) {
            long duration = Duration.between(start, Instant.now()).toMillis();
            
            // 记录失败指标
            recordFailureMetrics(signature, duration);

            log.error("API调用失败: {} 耗时: {}ms 错误: {}", signature, duration, e.getMessage());
            throw e;
        }
    }

    /**
     * 记录成功指标
     */
    private void recordSuccessMetrics(String signature, long duration) {
        // 增加成功计数
        Counter.builder("api.calls.total")
                .tag("method", signature)
                .tag("result", "success")
                .register(meterRegistry)
                .increment();

        // 记录响应时间
        meterRegistry.timer("api.call.duration", "method", signature)
                .record(duration, TimeUnit.MILLISECONDS);
    }

    /**
     * 记录失败指标
     */
    private void recordFailureMetrics(String signature, long duration) {
        // 增加失败计数
        Counter.builder("api.calls.total")
                .tag("method", signature)
                .tag("result", "failure")
                .register(meterRegistry)
                .increment();

        // 记录错误
        Counter.builder("api.errors.total")
                .tag("method", signature)
                .register(meterRegistry)
                .increment();
    }

    /**
     * 记录自定义指标（供其他服务调用）
     */
    public void recordCustomMetric(String name, String... tags) {
        Counter.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .increment();
    }

    /**
     * 记录自定义计时（供其他服务调用）
     */
    public void recordCustomTimer(String name, long duration, TimeUnit unit, String... tags) {
        Timer.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .record(duration, unit);
    }
}