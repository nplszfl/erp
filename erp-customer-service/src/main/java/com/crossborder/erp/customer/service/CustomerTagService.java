package com.crossborder.erp.customer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crossborder.erp.customer.entity.CustomerTag;
import com.crossborder.erp.customer.entity.CustomerTagRelation;
import com.crossborder.erp.customer.mapper.CustomerTagMapper;
import com.crossborder.erp.customer.mapper.CustomerTagRelationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户服务 - 客户标签管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerTagService extends ServiceImpl<CustomerTagMapper, CustomerTag> {

    private final CustomerTagRelationMapper tagRelationMapper;

    /**
     * 创建标签
     */
    public Long createTag(CustomerTag tag) {
        if (tag.getStatus() == null) {
            tag.setStatus("ACTIVE");
        }
        if (tag.getSortOrder() == null) {
            tag.setSortOrder(0);
        }
        if (tag.getColor() == null) {
            tag.setColor("#1890FF");
        }
        save(tag);
        log.info("创建客户标签: {}", tag.getName());
        return tag.getId();
    }

    /**
     * 更新标签
     */
    public void updateTag(CustomerTag tag) {
        updateById(tag);
        log.info("更新客户标签: {}", tag.getName());
    }

    /**
     * 获取所有启用的标签
     */
    public List<CustomerTag> getActiveTags() {
        return lambdaQuery()
            .eq(CustomerTag::getStatus, "ACTIVE")
            .orderByAsc(CustomerTag::getSortOrder)
            .list();
    }

    /**
     * 删除标签
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long id) {
        // 删除标签关联关系
        tagRelationMapper.delete(
            new LambdaQueryWrapper<CustomerTagRelation>()
                .eq(CustomerTagRelation::getTagId, id)
        );
        removeById(id);
        log.info("删除客户标签: id={}", id);
    }

    /**
     * 给客户添加标签
     */
    @Transactional(rollbackFor = Exception.class)
    public void addTagToCustomer(Long customerId, Long tagId) {
        // 检查是否已存在
        long count = tagRelationMapper.selectCount(
            new LambdaQueryWrapper<CustomerTagRelation>()
                .eq(CustomerTagRelation::getCustomerId, customerId)
                .eq(CustomerTagRelation::getTagId, tagId)
        );
        if (count == 0) {
            CustomerTagRelation relation = new CustomerTagRelation();
            relation.setCustomerId(customerId);
            relation.setTagId(tagId);
            tagRelationMapper.insert(relation);
            log.info("给客户添加标签: customerId={}, tagId={}", customerId, tagId);
        }
    }

    /**
     * 移除客户标签
     */
    public void removeTagFromCustomer(Long customerId, Long tagId) {
        tagRelationMapper.delete(
            new LambdaQueryWrapper<CustomerTagRelation>()
                .eq(CustomerTagRelation::getCustomerId, customerId)
                .eq(CustomerTagRelation::getTagId, tagId)
        );
        log.info("移除客户标签: customerId={}, tagId={}", customerId, tagId);
    }

    /**
     * 获取客户的所有标签
     */
    public List<CustomerTag> getTagsByCustomerId(Long customerId) {
        List<CustomerTagRelation> relations = tagRelationMapper.selectList(
            new LambdaQueryWrapper<CustomerTagRelation>()
                .eq(CustomerTagRelation::getCustomerId, customerId)
        );
        if (relations.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> tagIds = relations.stream()
            .map(CustomerTagRelation::getTagId)
            .collect(Collectors.toList());
        return listByIds(tagIds);
    }

    /**
     * 分页查询标签
     */
    public List<CustomerTag> listTags(String keyword) {
        LambdaQueryWrapper<CustomerTag> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(CustomerTag::getName, keyword)
                   .or().like(CustomerTag::getRemark, keyword);
        }
        wrapper.orderByAsc(CustomerTag::getSortOrder);
        return list(wrapper);
    }

    /**
     * 批量给客户添加标签
     */
    @Transactional(rollbackFor = Exception.class)
    public void addTagsToCustomer(Long customerId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            addTagToCustomer(customerId, tagId);
        }
    }

    /**
     * 清除客户所有标签
     */
    public void clearCustomerTags(Long customerId) {
        tagRelationMapper.delete(
            new LambdaQueryWrapper<CustomerTagRelation>()
                .eq(CustomerTagRelation::getCustomerId, customerId)
        );
        log.info("清除客户所有标签: customerId={}", customerId);
    }
}