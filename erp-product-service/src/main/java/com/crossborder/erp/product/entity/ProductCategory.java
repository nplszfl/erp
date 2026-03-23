package com.crossborder.erp.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类实体
 */
@Data
@TableName("t_product_category")
public class ProductCategory {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类ID（0为顶级分类）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 分类层级（1/2/3）
     */
    private Integer level;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 分类图标
     */
    private String icon;

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