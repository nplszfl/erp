package com.crossborder.pricing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 竞品数据实体
 */
@Data
@TableName("competitor_product")
public class CompetitorProduct {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 竞品名称 */
    private String competitorName;

    /** 竞品价格 */
    private BigDecimal price;

    /** 竞品URL */
    private String productUrl;

    /** 平台（Amazon、eBay、Shopee等） */
    private String platform;

    /** 评分（1-5） */
    private BigDecimal rating;

    /** 评论数 */
    private Long reviewCount;

    /** 销售量 */
    private Long salesVolume;

    /** 抓取时间 */
    private LocalDateTime scrapeTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
