package com.crossborder.erp.platform.controller;

import com.crossborder.erp.common.constant.PlatformType;
import com.crossborder.erp.common.result.Result;
import com.crossborder.erp.platform.service.PlatformOrderSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 平台同步Controller
 */
@RestController
@RequestMapping("/platform/sync")
@RequiredArgsConstructor
public class PlatformSyncController {

    private final PlatformOrderSyncService platformOrderSyncService;

    /**
     * 同步订单
     */
    @PostMapping("/orders")
    public Result<Void> syncOrders(
            @RequestParam String platform,
            @RequestParam String shopId,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime) {

        if (startTime == null) {
            startTime = LocalDateTime.now().minusHours(1);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        platformOrderSyncService.syncOrders(platform, shopId, startTime, endTime);
        return Result.success();
    }

    /**
     * 测试连接
     */
    @PostMapping("/test")
    public Result<Boolean> testConnection(@RequestParam String platform, @RequestParam String shopId) {
        boolean valid = platformOrderSyncService.validateConfig(platform, shopId);
        return Result.success(valid);
    }

    /**
     * 获取支持的平台列表
     */
    @GetMapping("/platforms")
    public Result<List<PlatformType>> getSupportedPlatforms() {
        List<PlatformType> platforms = platformOrderSyncService.getSupportedPlatforms();
        return Result.success(platforms);
    }
}
