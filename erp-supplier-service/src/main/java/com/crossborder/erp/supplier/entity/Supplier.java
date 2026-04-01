package com.crossborder.erp.supplier.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 供应商实体
 */
@Data
@TableName("t_supplier")
public class Supplier {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 供应商名称
     */
    private String name;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 联系邮箱
     */
    private String email;

    /**
     * 供应商类型（工厂/贸易商/代理商）
     */
    private String type;

    /**
     * 所在国家
     */
    private String country;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 主要供应产品类别
     */
    private String category;

    /**
     * 合作开始时间
     */
    private LocalDateTime cooperationStartAt;

    /**
     * 累计采购金额
     */
    private BigDecimal totalPurchaseAmount;

    /**
     * 采购订单数
     */
    private Integer totalOrders;

    /**
     * 评分（1-5）
     */
    private BigDecimal rating;

    /**
     * 供应商等级（A/B/C）
     */
    private String level;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（ACTIVE/INACTIVE/BLOCKED）
     */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}