package com.crossborder.tenant.dto.dashboard;

import lombok.*;
import java.time.LocalDate;

/**
 * 每日用量趋势
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyUsageDTO {

    private LocalDate date;
    private Long apiCalls;
    private Integer orders;
    private Long storageBytes;
}