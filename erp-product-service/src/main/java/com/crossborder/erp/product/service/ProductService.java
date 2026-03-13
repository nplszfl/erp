package com.crossborder.erp.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.product.entity.Product;

public interface ProductService {

    Product saveProduct(Product product);

    Product getProductById(Long id);

    Product getProductBySku(String sku);

    IPage<Product> pageProducts(Page<Product> page, String productName, Long categoryId);

    void deleteProduct(Long id);
}
