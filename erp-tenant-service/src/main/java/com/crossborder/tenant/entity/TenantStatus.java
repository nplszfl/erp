package com.crossborder.tenant.entity;

/**
 * 租户状态枚举
 */
public enum TenantStatus {
    PENDING,      // 待激活
    ACTIVE,       // 正常
    SUSPENDED,    // 已暂停
    EXPIRED,      // 已过期
    DELETED       // 已删除
}