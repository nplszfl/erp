package com.crossborder.erp.supplier.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crossborder.erp.supplier.entity.SupplierEvaluation;
import com.crossborder.erp.supplier.entity.Supplier;
import com.crossborder.erp.supplier.mapper.SupplierEvaluationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 供应商服务 - 供应商评估管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierEvaluationService extends ServiceImpl<SupplierEvaluationMapper, SupplierEvaluation> {

    private final SupplierService supplierService;

    /**
     * 创建评估记录
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createEvaluation(SupplierEvaluation evaluation) {
        // 计算综合评分
        BigDecimal totalScore = calculateTotalScore(evaluation);
        evaluation.setTotalScore(totalScore);
        
        // 计算评估结论
        evaluation.setConclusion(calculateConclusion(totalScore));
        evaluation.setEvaluateAt(LocalDateTime.now());
        
        save(evaluation);
        
        // 更新供应商的平均评分
        updateSupplierRating(evaluation.getSupplierId());
        
        log.info("创建供应商评估: supplierId={}, totalScore={}", 
            evaluation.getSupplierId(), totalScore);
        return evaluation.getId();
    }

    /**
     * 计算综合评分
     */
    private BigDecimal calculateTotalScore(SupplierEvaluation evaluation) {
        int quality = evaluation.getQualityScore() != null ? evaluation.getQualityScore() : 0;
        int delivery = evaluation.getDeliveryScore() != null ? evaluation.getDeliveryScore() : 0;
        int price = evaluation.getPriceScore() != null ? evaluation.getPriceScore() : 0;
        int service = evaluation.getServiceScore() != null ? evaluation.getServiceScore() : 0;
        int tech = evaluation.getTechScore() != null ? evaluation.getTechScore() : 0;
        
        // 质量占30%，交付占25%，价格占20%，服务占15%，技术占10%
        BigDecimal score = BigDecimal.valueOf(quality * 0.3 + delivery * 0.25 + 
            price * 0.2 + service * 0.15 + tech * 0.1);
        return score.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算评估结论
     */
    private String calculateConclusion(BigDecimal totalScore) {
        if (totalScore.compareTo(BigDecimal.valueOf(4.5)) >= 0) {
            return "EXCELLENT";
        } else if (totalScore.compareTo(BigDecimal.valueOf(4.0)) >= 0) {
            return "GOOD";
        } else if (totalScore.compareTo(BigDecimal.valueOf(3.0)) >= 0) {
            return "ACCEPTABLE";
        } else {
            return "POOR";
        }
    }

    /**
     * 更新供应商的平均评分
     */
    private void updateSupplierRating(Long supplierId) {
        List<SupplierEvaluation> evaluations = lambdaQuery()
            .eq(SupplierEvaluation::getSupplierId, supplierId)
            .orderByDesc(SupplierEvaluation::getEvaluateAt)
            .last("LIMIT 10")  // 取最近10次评估
            .list();
        
        if (!evaluations.isEmpty()) {
            BigDecimal avgScore = evaluations.stream()
                .map(SupplierEvaluation::getTotalScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(evaluations.size()), 2, RoundingMode.HALF_UP);
            
            Supplier supplier = supplierService.getById(supplierId);
            if (supplier != null) {
                supplier.setRating(avgScore);
                supplierService.updateById(supplier);
            }
        }
    }

    /**
     * 获取供应商的评估历史
     */
    public List<SupplierEvaluation> getBySupplierId(Long supplierId) {
        return lambdaQuery()
            .eq(SupplierEvaluation::getSupplierId, supplierId)
            .orderByDesc(SupplierEvaluation::getEvaluateAt)
            .list();
    }

    /**
     * 获取供应商最近一次评估
     */
    public SupplierEvaluation getLatestEvaluation(Long supplierId) {
        return lambdaQuery()
            .eq(SupplierEvaluation::getSupplierId, supplierId)
            .orderByDesc(SupplierEvaluation::getEvaluateAt)
            .last("LIMIT 1")
            .one();
    }

    /**
     * 分页查询评估记录
     */
    public List<SupplierEvaluation> listEvaluations(Long supplierId, String conclusion) {
        LambdaQueryWrapper<SupplierEvaluation> wrapper = new LambdaQueryWrapper<>();
        if (supplierId != null) {
            wrapper.eq(SupplierEvaluation::getSupplierId, supplierId);
        }
        if (StringUtils.hasText(conclusion)) {
            wrapper.eq(SupplierEvaluation::getConclusion, conclusion);
        }
        wrapper.orderByDesc(SupplierEvaluation::getEvaluateAt);
        return list(wrapper);
    }

    /**
     * 删除评估记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteEvaluation(Long id) {
        SupplierEvaluation evaluation = getById(id);
        if (evaluation != null) {
            removeById(id);
            // 重新计算供应商评分
            updateSupplierRating(evaluation.getSupplierId());
            log.info("删除供应商评估: id={}", id);
        }
    }
}