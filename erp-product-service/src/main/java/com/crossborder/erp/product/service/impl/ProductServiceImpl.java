package com.crossborder.erp.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.common.exception.BizException;
import com.crossborder.erp.product.entity.Product;
import com.crossborder.erp.product.mapper.ProductMapper;
import com.crossborder.erp.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 商品服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public Product saveProduct(Product product) {
        if (product.getId() == null) {
            productMapper.insert(product);
        } else {
            productMapper.updateById(product);
        }
        return product;
    }

    @Override
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public Product getProductBySku(String sku) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSku, sku);
        return productMapper.selectOne(wrapper);
    }

    @Override
    public IPage<Product> pageProducts(Page<Product> page, String productName, Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        if (productName != null && !productName.isEmpty()) {
            wrapper.like(Product::getName, productName);
        }
        
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(page, wrapper);
    }

    @Override
    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }
}