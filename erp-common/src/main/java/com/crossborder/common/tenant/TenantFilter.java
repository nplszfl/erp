package com.crossborder.common.tenant;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 多租户过滤器 - 从JWT Token中提取租户ID
 * 所有需要多租户支持的微服务都需要注册此过滤器
 */
@Component
@Order(1)
public class TenantFilter implements Filter {

    public static final String TENANT_HEADER = "X-Tenant-Id";
    public static final String TENANT_ATTRIBUTE = "tenantId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String tenantId = null;

        // 1. 优先从Header获取
        tenantId = httpRequest.getHeader(TENANT_HEADER);
        
        // 2. 如果没有，从请求属性获取（网关设置）
        if (tenantId == null) {
            tenantId = (String) httpRequest.getAttribute(TENANT_ATTRIBUTE);
        }

        if (tenantId != null) {
            TenantContextHolder.setTenant(tenantId);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }
}