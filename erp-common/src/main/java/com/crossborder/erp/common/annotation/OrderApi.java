package com.crossborder.erp.order.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 订单API接口注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "订单管理", description = "订单相关接口")
@Operation(summary = "订单管理", description = "订单的增删改查操作")
@Parameter(name = "Authorization", description = "认证Token", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
public @interface OrderApi {
    /**
     * 操作描述
     */
    @Operation
    String value() default "";
}
