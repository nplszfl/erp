package com.crossborder.erp.order.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 性能监控切面
 * 记录方法执行时间
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PerformanceMonitorAspect {

    private final MeterRegistry meterRegistry;

    @Around("@annotation(io.micrometer.core.annotation.Timed)")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String metricName = className + "." + methodName;

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录执行时间
            recordExecutionTime(metricName, duration);

            // 记录到日志
            if (duration > 1000) {
                log.warn("方法执行时间过长: {} - {}ms", metricName, duration);
            } else {
                log.debug("方法执行时间: {} - {}ms", metricName, duration);
            }

            return result;
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            recordExecutionTime(metricName + ".error", duration);

            log.error("方法执行出错: {} - {}ms", metricName, duration, e);
            throw e;
        }
    }

    /**
     * 记录执行时间到Micrometer
     */
    private void recordExecutionTime(String metricName, long durationMs) {
        Timer.builder(metricName)
                .description("Method execution time")
                .register(meterRegistry)
                .record(durationMs, TimeUnit.MILLISECONDS);
    }
}
