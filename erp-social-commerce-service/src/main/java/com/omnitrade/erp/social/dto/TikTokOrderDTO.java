package com.omnitrade.erp.social.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TikTok 订单DTO
 */
@Data
@Builder
public class TikTokOrderDTO {
    
    private Long id;
    private String orderId;
    private String shopId;
    private String status;
    private BigDecimal orderAmount;
    private BigDecimal productAmount;
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private String buyerId;
    private String buyerUsername;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String country;
    private String state;
    private String city;
    private String postalCode;
    private Integer itemQuantity;
    private String items;
    private String trackingNumber;
    private String shippingCarrier;
    private LocalDateTime shippedTime;
    private LocalDateTime deliveredTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime payTime;
    private Long liveStreamId;
    private Long influencerId;
    private BigDecimal commissionAmount;
    private BigDecimal commissionRate;
    private String remark;
    private String createdAt;
    private String updatedAt;
}