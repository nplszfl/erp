package com.crossborder.erp.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品SKU实体
 */
@Data
@TableName("t_product_sku")
public class ProductSku {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * SKU编码
     */
    private String sku;

    /**
     * SKU名称
     */
    private String name;

    /**
     * 条码
     */
    private String barCode;

    /**
     * 规格属性（JSON格式，如：{"color":"红色","size":"XL"}）
     */
    private String attributes;

    /**
     * 采购成本
     */
    @TableField("cost_price")
    private BigDecimal costPrice;

    /**
     * 销售价格
     */
    @TableField("sale_price")
    private BigDecimal salePrice;

    /**
     * 重量（克）
     */
    private BigDecimal weight;

    /**
     * 长度（cm）
     */
    private BigDecimal length;

    /**
     * 宽度（cm）
     */
    private BigDecimal width;

    /**
     * 高度（cm）
     */
    private BigDecimal height;

    /**
     * 库存数量
     */
    @TableField("stock_quantity")
    private Integer stockQuantity;

    /**
     * 安全库存
     */
    @TableField("safety_stock")
    private Integer safetyStock;

    /**
     * 状态（0禁用 1启用）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}