package com.crossborder.erp.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.common.exception.BusinessException;
import com.crossborder.erp.product.entity.*;
import com.crossborder.erp.product.mapper.*;
import com.crossborder.erp.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductSkuMapper skuMapper;
    private final ProductImageMapper imageMapper;
    private final ProductCategoryMapper categoryMapper;
    private final ProductPlatformSkuMapper platformSkuMapper;

    @Override
    public Page<Product> listProducts(String keyword, Long categoryId, String status,
                                    int page, int size) {
        Page<Product> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                               .or().like(Product::getSku, keyword)
                               .or().like(Product::getPlatformSku, keyword));
        }
        
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Product::getStatus, status);
        }
        
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(pageObj, wrapper);
    }

    @Override
    public Product getProduct(Long productId) {
        return productMapper.selectById(productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(Product product, List<ProductSku> skus, List<ProductImage> images) {
        // 验证分类
        ProductCategory category = categoryMapper.selectById(product.getCategoryId());
        if (category == null) {
            throw new BusinessException("商品分类不存在");
        }
        
        // 生成商品编号
        product.setProductNo(generateProductNo());
        product.setStatus("DRAFT"); // 草稿状态
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insert(product);
        
        // 保存SKU
        if (skus != null && !skus.isEmpty()) {
            for (ProductSku sku : skus) {
                sku.setProductId(product.getId());
                sku.setSku(generateSku(product.getProductNo(), skus.indexOf(sku)));
                sku.setStockQuantity(0);
                sku.setCreateTime(LocalDateTime.now());
                skuMapper.insert(sku);
            }
        }
        
        // 保存图片
        if (images != null && !images.isEmpty()) {
            for (ProductImage image : images) {
                image.setProductId(product.getId());
                image.setSortOrder(images.indexOf(image));
                image.setCreateTime(LocalDateTime.now());
                imageMapper.insert(image);
            }
        }
        
        log.info("创建商品成功: {} ({})", product.getName(), product.getProductNo());
        return product.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Product product, List<ProductSku> skus, List<ProductImage> images) {
        Product existing = productMapper.selectById(product.getId());
        if (existing == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 更新商品基本信息
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        
        // 删除原有SKU
        LambdaQueryWrapper<ProductSku> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.eq(ProductSku::getProductId, product.getId());
        skuMapper.delete(skuWrapper);
        
        // 保存新SKU
        if (skus != null && !skus.isEmpty()) {
            for (ProductSku sku : skus) {
                if (sku.getId() != null) {
                    sku.setUpdateTime(LocalDateTime.now());
                    skuMapper.updateById(sku);
                } else {
                    sku.setProductId(product.getId());
                    sku.setSku(generateSku(existing.getProductNo(), skus.indexOf(sku)));
                    sku.setStockQuantity(0);
                    sku.setCreateTime(LocalDateTime.now());
                    skuMapper.insert(sku);
                }
            }
        }
        
        // 删除原有图片
        LambdaQueryWrapper<ProductImage> imgWrapper = new LambdaQueryWrapper<>();
        imgWrapper.eq(ProductImage::getProductId, product.getId());
        imageMapper.delete(imgWrapper);
        
        // 保存新图片
        if (images != null && !images.isEmpty()) {
            for (ProductImage image : images) {
                image.setProductId(product.getId());
                image.setSortOrder(images.indexOf(image));
                image.setCreateTime(LocalDateTime.now());
                imageMapper.insert(image);
            }
        }
        
        log.info("更新商品成功: {}", product.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 检查商品是否上架
        if ("ON_SHELF".equals(product.getStatus())) {
            throw new BusinessException("商品已上架，无法删除");
        }
        
        // 删除SKU
        LambdaQueryWrapper<ProductSku> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.eq(ProductSku::getProductId, productId);
        skuMapper.delete(skuWrapper);
        
        // 删除图片
        LambdaQueryWrapper<ProductImage> imgWrapper = new LambdaQueryWrapper<>();
        imgWrapper.eq(ProductImage::getProductId, productId);
        imageMapper.delete(imgWrapper);
        
        // 删除平台SKU映射
        LambdaQueryWrapper<ProductPlatformSku> pSkuWrapper = new LambdaQueryWrapper<>();
        pSkuWrapper.eq(ProductPlatformSku::getProductId, productId);
        platformSkuMapper.delete(pSkuWrapper);
        
        // 删除商品
        productMapper.deleteById(productId);
        
        log.info("删除商品成功: {}", product.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 检查商品信息完整性
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new BusinessException("商品名称不能为空");
        }
        
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("商品价格必须大于0");
        }
        
        // 检查SKU
        LambdaQueryWrapper<ProductSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductSku::getProductId, productId);
        List<ProductSku> skus = skuMapper.selectList(wrapper);
        
        if (skus == null || skus.isEmpty()) {
            throw new BusinessException("商品必须至少有一个SKU");
        }
        
        // 检查图片
        LambdaQueryWrapper<ProductImage> imgWrapper = new LambdaQueryWrapper<>();
        imgWrapper.eq(ProductImage::getProductId, productId);
        List<ProductImage> images = imageMapper.selectList(imgWrapper);
        
        if (images == null || images.isEmpty()) {
            throw new BusinessException("商品必须至少有一张图片");
        }
        
        // 上架商品
        product.setStatus("ON_SHELF");
        product.setPublishTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        
        log.info("上架商品成功: {}", product.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offShelfProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        product.setStatus("OFF_SHELF");
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        
        log.info("下架商品成功: {}", product.getName());
    }

    @Override
    public List<ProductSku> listSkus(Long productId) {
        LambdaQueryWrapper<ProductSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductSku::getProductId, productId);
        return skuMapper.selectList(wrapper);
    }

    @Override
    public List<ProductImage> listImages(Long productId) {
        LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImage::getProductId, productId)
               .orderByAsc(ProductImage::getSortOrder);
        return imageMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPlatformSku(Long productId, String platform, String platformSku,
                                String platformProductId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 检查是否已绑定
        LambdaQueryWrapper<ProductPlatformSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductPlatformSku::getProductId, productId)
               .eq(ProductPlatformSku::getPlatform, platform);
        ProductPlatformSku existing = platformSkuMapper.selectOne(wrapper);
        
        if (existing != null) {
            existing.setPlatformSku(platformSku);
            existing.setPlatformProductId(platformProductId);
            existing.setUpdateTime(LocalDateTime.now());
            platformSkuMapper.updateById(existing);
        } else {
            ProductPlatformSku newMapping = new ProductPlatformSku();
            newMapping.setProductId(productId);
            newMapping.setPlatform(platform);
            newMapping.setPlatformSku(platformSku);
            newMapping.setPlatformProductId(platformProductId);
            newMapping.setCreateTime(LocalDateTime.now());
            newMapping.setUpdateTime(LocalDateTime.now());
            platformSkuMapper.insert(newMapping);
        }
        
        log.info("绑定平台SKU成功: {} -> {}", platform, platformSku);
    }

    @Override
    public ProductPlatformSku getPlatformSku(Long productId, String platform) {
        LambdaQueryWrapper<ProductPlatformSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductPlatformSku::getProductId, productId)
               .eq(ProductPlatformSku::getPlatform, platform);
        return platformSkuMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStock(Long skuId, Integer quantity, String operation) {
        ProductSku sku = skuMapper.selectById(skuId);
        if (sku == null) {
            throw new BusinessException("SKU不存在");
        }
        
        if ("INCREASE".equals(operation)) {
            sku.setStockQuantity(sku.getStockQuantity() + quantity);
        } else if ("DECREASE".equals(operation)) {
            if (sku.getStockQuantity() < quantity) {
                throw new BusinessException("库存不足，当前库存: " + sku.getStockQuantity());
            }
            sku.setStockQuantity(sku.getStockQuantity() - quantity);
        } else {
            throw new BusinessException("不支持的操作: " + operation);
        }
        
        sku.setUpdateTime(LocalDateTime.now());
        skuMapper.updateById(sku);
        
        log.info("更新SKU库存: {} {} {}", sku.getSku(), operation, quantity);
    }

    @Override
    public List<ProductCategory> listCategories(Long parentId) {
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        if (parentId == null) {
            wrapper.isNull(ProductCategory::getParentId);
        } else {
            wrapper.eq(ProductCategory::getParentId, parentId);
        }
        wrapper.orderByAsc(ProductCategory::getSortOrder);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(ProductCategory category) {
        // 验证父分类
        if (category.getParentId() != null) {
            ProductCategory parent = categoryMapper.selectById(category.getParentId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
        }
        
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(category);
        
        log.info("创建商品分类成功: {}", category.getName());
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(ProductCategory category) {
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(category);
        log.info("更新商品分类成功: {}", category.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long categoryId) {
        // 检查是否有子分类
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategory::getParentId, categoryId);
        Long childCount = categoryMapper.selectCount(wrapper);
        if (childCount > 0) {
            throw new BusinessException("分类下有子分类，无法删除");
        }
        
        // 检查是否有商品
        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.eq(Product::getCategoryId, categoryId);
        Long productCount = productMapper.selectCount(productWrapper);
        if (productCount > 0) {
            throw new BusinessException("分类下有商品，无法删除");
        }
        
        categoryMapper.deleteById(categoryId);
        log.info("删除商品分类成功: ID={}", categoryId);
    }

    /**
     * 生成商品编号
     */
    private String generateProductNo() {
        return "PROD" + System.currentTimeMillis();
    }

    /**
     * 生成SKU
     */
    private String generateSku(String productNo, int index) {
        return productNo + "-" + String.format("%03d", index + 1);
    }
}
