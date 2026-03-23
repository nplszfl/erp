package com.crossborder.erp.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品图片实体
 */
@Data
@TableName("t_product_image")
public class ProductImage {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 缩略图URL
     */
    @TableField("thumb_url")
    private String thumbUrl;

    /**
     * 图片类型（1主图 2详情图 3包装图）
     */
    @TableField("image_type")
    private Integer imageType;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}