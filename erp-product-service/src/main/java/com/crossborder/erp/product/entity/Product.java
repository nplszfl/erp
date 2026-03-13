package com.crossborder.erp.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_product")
public class Product {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("internal_sku")
    private String internalSku;

    @TableField("product_name")
    private String productName;

    @TableField("product_name_en")
    private String productNameEn;

    @TableField("category_id")
    private Long categoryId;

    private String brand;

    @TableField("main_image")
    private String mainImage;

    private String images;

    private String description;

    @TableField("description_en")
    private String descriptionEn;

    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableLogic
    private Integer deleted;
}
