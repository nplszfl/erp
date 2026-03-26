package com.omnitrade.erp.social.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CooperationDTO {
    
    private Long id;
    
    @NotNull(message = "达人ID不能为空")
    private Long influencerId;
    
    @NotBlank(message = "产品ID不能为空")
    private String productId;
    
    @NotBlank(message = "产品名称不能为空")
    private String productName;
    
    @NotNull(message = "合作类型不能为空")
    private String type;
    
    private String status;
    
    private String plannedStartDate;
    
    private String plannedEndDate;
    
    private String actualStartDate;
    
    private String actualEndDate;
    
    private BigDecimal agreedPrice;
    
    private BigDecimal actualCost;
    
    private BigDecimal commissionRate;
    
    private Integer expectedViews;
    
    private Integer actualViews;
    
    private Integer expectedSales;
    
    private Integer actualSales;
    
    private BigDecimal expectedGmv;
    
    private BigDecimal actualGmv;
    
    private String content;
    
    private String remark;
    
    private String createdAt;
    private String updatedAt;
}