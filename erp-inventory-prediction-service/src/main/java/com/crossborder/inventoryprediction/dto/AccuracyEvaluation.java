package com.crossborder.inventoryprediction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 预测准确性评估结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccuracyEvaluation {

    /** 产品ID */
    private Long productId;

    /** 产品编码 */
    private String productCode;

    /** 评估开始日期 */
    private LocalDate startDate;

    /** 评估结束日期 */
    private LocalDate endDate;

    /** 评估天数 */
    private int evaluationDays;

    /** 平均绝对百分比误差 (MAPE) - 越小越好 */
    private double mape;

    /** 平均绝对误差 (MAE) */
    private double mae;

    /** 均方根误差 (RMSE) */
    private double rmse;

    /** 预测准确率 (1 - MAPE) */
    private double accuracy;

    /** 评估等级: EXCELLENT, GOOD, FAIR, POOR */
    private String grade;

    /** 预测总销量 */
    private long totalPredicted;

    /** 实际总销量 */
    private long totalActual;

    /** 偏差百分比 */
    private double deviationPercent;

    /** 每日详细对比 */
    private List<DailyComparison> dailyComparisons;

    /** 评估时间 */
    private String evaluationTime;

    /**
     * 每日预测与实际对比
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyComparison {
        private LocalDate date;
        private long predicted;
        private long actual;
        private double error;
        private double absError;
        private double percentageError;
    }

    /**
     * 获取等级描述
     */
    public String getGradeDescription() {
        return switch (grade) {
            case "EXCELLENT" -> "优秀 - 预测非常准确";
            case "GOOD" -> "良好 - 预测比较准确";
            case "FAIR" -> "一般 - 存在一定偏差";
            case "POOR" -> "较差 - 需要优化预测模型";
            default -> "未知";
        };
    }
}