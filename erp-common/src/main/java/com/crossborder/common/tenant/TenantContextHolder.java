package com.crossborder.common.tenant;

import lombok.Getter;

/**
 * 租户上下文 - ThreadLocal存储当前请求的租户信息
 * 所有微服务共享此组件
 */
@Getter
public class TenantContextHolder {
    
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();
    
    public static void setTenant(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }
    
    public static String getTenant() {
        return CURRENT_TENANT.get();
    }
    
    public static void setUser(String userId) {
        CURRENT_USER.set(userId);
    }
    
    public static String getUser() {
        return CURRENT_USER.get();
    }
    
    public static void clear() {
        CURRENT_TENANT.remove();
        CURRENT_USER.remove();
    }
    
    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }
}