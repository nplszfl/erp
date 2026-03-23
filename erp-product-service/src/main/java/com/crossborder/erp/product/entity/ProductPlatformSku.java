package com.crossborder.erp.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品平台SKU实体
 * 记录商品在各平台的SKU信息
 */
@Data
@TableName("t_product_platform_sku")
public class ProductPlatformSku {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * SKU ID
     */
    @TableField("sku_id")
    private Long skuId;

    /**
     * 平台类型（amazon/ebay/shopee/lazada/tiktok）
     */
    private String platform;

    /**
     * 平台店铺ID
     */
    @TableField("shop_id")
    private String shopId;

    /**
     * 平台SKU
     */
    @TableField("platform_sku")
    private String platformSku;

    /**
     * 平台商品ID
     */
    @TableField("platform_product_id")
    private String platformProductId;

    /**
     * 平台商品URL
     */
    @TableField("product_url")
    private String productUrl;

    /**
     * 售价
     */
    @TableField("sale_price")
    private BigDecimal salePrice;

    /**
     * 原价
     */
    @TableField("original_price")
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    @TableField("stock_quantity")
    private Integer stockQuantity;

    /**
     * 状态（0下架 1上架）
     */
    private Integer status;

    /**
     * 同步时间
     */
    @TableField("sync_time")
    private LocalDateTime syncTime;

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