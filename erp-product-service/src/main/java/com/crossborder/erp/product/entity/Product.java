package com.crossborder.erp.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("t_product")
public class Product {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商品名称
     */
    @TableField("product_name")
    private String name;

    /**
     * SKU编码
     */
    private String sku;

    /**
     * 平台SKU（各平台自己的SKU）
     */
    @TableField("platform_sku")
    private String platformSku;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 主图
     */
    @TableField("main_image")
    private String mainImage;

    /**
     * 图片列表（JSON格式）
     */
    private String images;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 英文描述
     */
    @TableField("description_en")
    private String descriptionEn;

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
     * 成本价
     */
    @TableField("cost_price")
    private BigDecimal costPrice;

    /**
     * 销售价
     */
    @TableField("sale_price")
    private BigDecimal salePrice;

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

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}