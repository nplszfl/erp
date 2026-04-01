package com.crossborder.erp.customer.controller;

import com.crossborder.erp.customer.entity.CustomerGroup;
import com.crossborder.erp.customer.service.CustomerGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户分组管理控制器
 */
@RestController
@RequestMapping("/customer/group")
@RequiredArgsConstructor
public class CustomerGroupController {

    private final CustomerGroupService customerGroupService;

    /**
     * 创建客户分组
     */
    @PostMapping("/create")
    public Long create(@RequestBody CustomerGroup group) {
        return customerGroupService.createGroup(group);
    }

    /**
     * 更新客户分组
     */
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody CustomerGroup group) {
        group.setId(id);
        customerGroupService.updateGroup(group);
    }

    /**
     * 获取分组详情
     */
    @GetMapping("/{id}")
    public CustomerGroup get(@PathVariable Long id) {
        return customerGroupService.getById(id);
    }

    /**
     * 获取所有启用的分组
     */
    @GetMapping("/list")
    public List<CustomerGroup> list(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return customerGroupService.listGroups(keyword);
        }
        return customerGroupService.getActiveGroups();
    }

    /**
     * 删除分组
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        customerGroupService.deleteGroup(id);
    }
}