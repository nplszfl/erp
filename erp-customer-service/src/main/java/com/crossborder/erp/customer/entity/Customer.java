package com.crossborder.erp.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户实体 - 跨境电商客户管理
 */
@Data
@TableName("t_customer")
public class Customer {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 客户姓名
     */
    private String name;

    /**
     * 客户邮箱
     */
    private String email;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 客户来源平台
     */
    private String platform;

    /**
     * 平台客户ID
     */
    private String platformCustomerId;

    /**
     * 国家/地区
     */
    private String country;

    /**
     * 省份/州
     */
    private String state;

    /**
     * 城市
     */
    private String city;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 客户等级（VIP/NORMAL/NEW）
     */
    private String level;

    /**
     * 订单总数
     */
    private Integer totalOrders;

    /**
     * 累计消费金额
     */
    private BigDecimal totalSpent;

    /**
     * 平均订单金额
     */
    private BigDecimal avgOrderValue;

    /**
     * 最后下单时间
     */
    private LocalDateTime lastOrderAt;

    /**
     * 客户备注
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