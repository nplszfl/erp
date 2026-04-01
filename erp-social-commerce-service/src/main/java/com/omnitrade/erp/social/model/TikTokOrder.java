package com.omnitrade.erp.social.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TikTok 订单实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tiktok_order")
public class TikTokOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * TikTok订单ID
     */
    @Column(unique = true, nullable = false)
    private String orderId;
    
    /**
     * 店铺ID
     */
    @Column(nullable = false)
    private String shopId;
    
    /**
     * 订单状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    /**
     * 订单金额
     */
    @Column(precision = 12, scale = 2)
    private BigDecimal orderAmount;
    
    /**
     * 商品金额
     */
    @Column(precision = 12, scale = 2)
    private BigDecimal productAmount;
    
    /**
     * 运费
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal shippingFee;
    
    /**
     * 税费
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal taxAmount;
    
    /**
     * 折扣金额
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;
    
    /**
     * 买家ID
     */
    private String buyerId;
    
    /**
     * 买家用户名
     */
    private String buyerUsername;
    
    /**
     * 收货人姓名
     */
    private String recipientName;
    
    /**
     * 收货人电话
     */
    private String recipientPhone;
    
    /**
     * 收货地址
     */
    @Column(length = 500)
    private String shippingAddress;
    
    /**
     * 国家
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
     * 邮编
     */
    private String postalCode;
    
    /**
     * 商品数量
     */
    private Integer itemQuantity;
    
    /**
     * 商品信息 (JSON格式存储)
     */
    @Column(length = 2000)
    private String items;
    
    /**
     * 物流单号
     */
    private String trackingNumber;
    
    /**
     * 物流公司
     */
    private String shippingCarrier;
    
    /**
     * 发货时间
     */
    private LocalDateTime shippedTime;
    
    /**
     * 签收时间
     */
    private LocalDateTime deliveredTime;
    
    /**
     * 订单创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createTime;
    
    /**
     * 订单更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 本地关联的直播ID
     */
    private Long liveStreamId;
    
    /**
     * 本地关联的达人ID
     */
    private Long influencerId;
    
    /**
     * 佣金金额
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal commissionAmount;
    
    /**
     * 佣金比率
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal commissionRate;
    
    /**
     * 备注
     */
    @Column(length = 1000)
    private String remark;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum OrderStatus {
        PENDING,        // 待支付
        PAID,           // 已支付
        CONFIRMED,      // 已确认
        PROCESSING,     // 处理中
        SHIPPED,        // 已发货
        DELIVERED,      // 已送达
        COMPLETED,      // 已完成
        CANCELLED,      // 已取消
        REFUNDED,       // 已退款
        PARTIAL_REFUND  // 部分退款
    }
}