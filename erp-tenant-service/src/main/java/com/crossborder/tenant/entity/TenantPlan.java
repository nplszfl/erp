package com.crossborder.tenant.entity;

/**
 * 订阅计划枚举
 */
public enum TenantPlan {
    STARTER(2999, 3000, 1, 100),
    PROFESSIONAL(4999, 15000, 3, 500),
    BUSINESS(12999, 50000, 5, 2000),
    ENTERPRISE(0, 0, 999, 999999);

    private final int price;
    private final int orderLimit;
    private final int platformLimit;
    private final int aiQuota;

    TenantPlan(int price, int orderLimit, int platformLimit, int aiQuota) {
        this.price = price;
        this.orderLimit = orderLimit;
        this.platformLimit = platformLimit;
        this.aiQuota = aiQuota;
    }

    public int getPrice() { return price; }
    public int getOrderLimit() { return orderLimit; }
    public int getPlatformLimit() { return platformLimit; }
    public int getAiQuota() { return aiQuota; }
}