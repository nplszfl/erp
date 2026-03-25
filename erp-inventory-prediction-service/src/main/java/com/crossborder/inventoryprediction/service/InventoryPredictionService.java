package com.crossborder.inventoryprediction.service;

import com.crossborder.inventoryprediction.dto.AccuracyEvaluation;
import com.crossborder.inventoryprediction.dto.PredictionRequest;
import com.crossborder.inventoryprediction.dto.PredictionResponse;
import com.crossborder.inventoryprediction.dto.ReplenishmentSuggestion;
import com.crossborder.inventoryprediction.dto.ServiceStatistics;

import java.time.LocalDate;
import java.util.List;

/**
 * 库存预测服务
 *
 * 核心算法：
 * 1. 时间序列预测 - 使用Prophet/ARIMA模型预测未来销量
 * 2. 季节性分析 - 识别季节性模式
 * 3. 趋势分解 - 分解为趋势、季节性、残差
 * 4. 异常检测 - 识别销售异常
 */
public interface InventoryPredictionService {

    /**
     * 预测未来销量
     *
     * @param request 预测请求
     * @return 预测结果
     */
    PredictionResponse predictSales(PredictionRequest request);

    /**
     * 批量预测多个产品的销量
     */
    List<PredictionResponse> batchPredictSales(List<PredictionRequest> requests);

    /**
     * 获取补货建议
     *
     * @param productId 产品ID
     * @param predictionDays 预测天数
     * @return 补货建议
     */
    ReplenishmentSuggestion getReplenishmentSuggestion(Long productId, int predictionDays);

    /**
     * 计算安全库存
     *
     * @param productId 产品ID
     * @return 安全库存数量
     */
    int calculateSafetyStock(Long productId);

    /**
     * 预警库存不足
     *
     * @param predictionDays 预测天数
     * @return 库存不足的产品列表
     */
    List<Long> warnLowStock(int predictionDays);

    /**
     * 计算库存周转率
     *
     * @param productId 产品ID
     * @param days 计算天数
     * @return 周转率
     */
    double calculateInventoryTurnover(Long productId, int days);

    /**
     * 优化库存结构
     *
     * 基于周转率和需求，优化库存配置
     */
    void optimizeInventoryStructure();

    /**
     * 获取服务统计信息
     *
     * @return 服务统计数据
     */
    ServiceStatistics getStatistics();

    /**
     * 重置统计数据
     */
    void resetStatistics();

    /**
     * 评估预测准确性
     * 对比历史预测与实际销售，计算各种误差指标
     *
     * @param productId 产品ID
     * @param startDate 评估开始日期
     * @param endDate 评估结束日期
     * @return 准确性评估结果
     */
    AccuracyEvaluation evaluateAccuracy(Long productId, LocalDate startDate, LocalDate endDate);

    /**
     * 批量评估多个产品的预测准确性
     *
     * @param productIds 产品ID列表
     * @param startDate 评估开始日期
     * @param endDate 评估结束日期
     * @return 准确性评估结果列表
     */
    List<AccuracyEvaluation> batchEvaluateAccuracy(List<Long> productIds, LocalDate startDate, LocalDate endDate);

    /**
     * 获取模型准确性趋势
     * 分析一段时间内预测准确性的变化趋势
     *
     * @param productId 产品ID
     * @param days 历史天数
     * @return 准确性趋势数据
     */
    List<AccuracyEvaluation> getAccuracyTrend(Long productId, int days);
}
