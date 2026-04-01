package com.crossborder.erp.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客户标签关联关系
 */
@Data
@TableName("t_customer_tag_relation")
public class CustomerTagRelation {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 标签ID
     */
    private Long tagId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}