package com.crossborder.erp.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crossborder.erp.customer.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}