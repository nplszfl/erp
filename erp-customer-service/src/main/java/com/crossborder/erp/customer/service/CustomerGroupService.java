package com.crossborder.erp.customer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crossborder.erp.customer.entity.CustomerGroup;
import com.crossborder.erp.customer.mapper.CustomerGroupMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 客户服务 - 客户分组管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerGroupService extends ServiceImpl<CustomerGroupMapper, CustomerGroup> {

    /**
     * 创建客户分组
     */
    public Long createGroup(CustomerGroup group) {
        if (group.getStatus() == null) {
            group.setStatus("ACTIVE");
        }
        if (group.getSortOrder() == null) {
            group.setSortOrder(0);
        }
        save(group);
        log.info("创建客户分组: {}", group.getName());
        return group.getId();
    }

    /**
     * 更新客户分组
     */
    public void updateGroup(CustomerGroup group) {
        updateById(group);
        log.info("更新客户分组: {}", group.getName());
    }

    /**
     * 获取所有启用的分组
     */
    public List<CustomerGroup> getActiveGroups() {
        return lambdaQuery()
            .eq(CustomerGroup::getStatus, "ACTIVE")
            .orderByAsc(CustomerGroup::getSortOrder)
            .list();
    }

    /**
     * 根据名称查询分组
     */
    public CustomerGroup getByName(String name) {
        return lambdaQuery()
            .eq(CustomerGroup::getName, name)
            .one();
    }

    /**
     * 分页查询分组
     */
    public List<CustomerGroup> listGroups(String keyword) {
        LambdaQueryWrapper<CustomerGroup> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(CustomerGroup::getName, keyword)
                   .or().like(CustomerGroup::getDescription, keyword);
        }
        wrapper.orderByAsc(CustomerGroup::getSortOrder);
        return list(wrapper);
    }

    /**
     * 删除分组
     */
    public void deleteGroup(Long id) {
        removeById(id);
        log.info("删除客户分组: id={}", id);
    }
}