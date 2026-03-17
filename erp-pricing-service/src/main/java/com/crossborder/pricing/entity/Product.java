package com.crossborder.pricing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品实体
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 产品编码 */
    private String productCode;

    /** 产品名称 */
    private String productName;

    /** 成本价 */
    private BigDecimal costPrice;

    /** 当前售价 */
    private BigDecimal sellingPrice;

    /** SKU */
    private String sku;

    /** 平台ID（Amazon、eBay、Shopee等） */
    private Long platformId;

    /** 平台产品ID */
    private String platformProductId;

    /** 状态（1-上架，0-下架） */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
