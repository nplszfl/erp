package com.crossborder.erp.product;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.product.entity.*;
import com.crossborder.erp.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 商品服务单元测试
 */
@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryMapper categoryMapper;

    @Test
    public void testCreateProduct() {
        Product product = new Product();
        product.setName("测试商品");
        product.setCategoryId(1L);
        product.setPrice(new BigDecimal("99.99"));
        product.setDescription("测试商品描述");

        ProductSku sku1 = new ProductSku();
        sku1.setSku("TEST-SKU-001");
        sku1.setPrice(new BigDecimal("99.99"));
        sku1.setColor("红色");
        sku1.setSize("L");
        sku1.setStockQuantity(100);

        ProductSku sku2 = new ProductSku();
        sku2.setSku("TEST-SKU-002");
        sku2.setPrice(new BigDecimal("99.99"));
        sku2.setColor("红色");
        sku2.setSize("XL");
        sku2.setStockQuantity(50);

        ProductImage image1 = new ProductImage();
        image1.setImageUrl("https://example.com/image1.jpg");
        image1.setIsMain(true);

        ProductImage image2 = new ProductImage();
        image2.setImageUrl("https://example.com/image2.jpg");
        image2.setIsMain(false);

        Long productId = productService.createProduct(
                product,
                Arrays.asList(sku1, sku2),
                Arrays.asList(image1, image2)
        );

        assertNotNull(productId);
        assertTrue(productId > 0);
    }

    @Test
    public void testListProducts() {
        Page<Product> page = productService.listProducts(null, null, null, 1, 10);
        assertNotNull(page);
        assertNotNull(page.getRecords());
    }

    @Test
    public void testGetProduct() {
        Product product = productService.getProduct(1L);
        assertNotNull(product);
        assertNotNull(product.getName());
    }

    @Test
    public void testPublishProduct() {
        Long productId = 1L;
        productService.publishProduct(productId);

        Product product = productService.getProduct(productId);
        assertEquals("ON_SHELF", product.getStatus());
    }

    @Test
    public void testOffShelfProduct() {
        Long productId = 1L;
        productService.offShelfProduct(productId);

        Product product = productService.getProduct(productId);
        assertEquals("OFF_SHELF", product.getStatus());
    }

    @Test
    public void testListSkus() {
        List<ProductSku> skus = productService.listSkus(1L);
        assertNotNull(skus);
        assertFalse(skus.isEmpty());
    }

    @Test
    public void testListImages() {
        List<ProductImage> images = productService.listImages(1L);
        assertNotNull(images);
        assertFalse(images.isEmpty());
    }

    @Test
    public void testBindPlatformSku() {
        Long productId = 1L;
        String platform = "amazon";
        String platformSku = "AMZ-TEST-001";
        String platformProductId = "B0123456789";

        productService.bindPlatformSku(productId, platform, platformSku, platformProductId);

        ProductPlatformSku mapping = productService.getPlatformSku(productId, platform);
        assertNotNull(mapping);
        assertEquals(platformSku, mapping.getPlatformSku());
    }

    @Test
    public void testUpdateStock() {
        // 先获取SKU列表
        List<ProductSku> skus = productService.listSkus(1L);
        if (!skus.isEmpty()) {
            Long skuId = skus.get(0).getId();

            // 增加库存
            productService.updateStock(skuId, 10, "INCREASE");

            ProductSku sku = skus.get(0);
            assertTrue(sku.getStockQuantity() >= 10);
        }
    }

    @Test
    public void testListCategories() {
        List<ProductCategory> categories = productService.listCategories(null);
        assertNotNull(categories);
        assertFalse(categories.isEmpty());
    }

    @Test
    public void testCreateCategory() {
        ProductCategory category = new ProductCategory();
        category.setName("测试分类");
        category.setCode("TEST");
        category.setSortOrder(0);

        Long categoryId = productService.createCategory(category);
        assertNotNull(categoryId);
        assertTrue(categoryId > 0);
    }

    @Test
    public void testDeleteCategory() {
        // 先创建一个临时分类
        ProductCategory category = new ProductCategory();
        category.setName("临时分类");
        category.setCode("TEMP");
        category.setSortOrder(999);
        Long categoryId = productService.createCategory(category);

        // 删除分类
        productService.deleteCategory(categoryId);

        // 验证已删除
        ProductCategory deleted = categoryMapper.selectById(categoryId);
        assertNull(deleted);
    }

    @Test
    public void testUpdateProduct() {
        Product product = productService.getProduct(1L);
        assertNotNull(product);

        product.setName("更新后的商品名称");
        product.setPrice(new BigDecimal("199.99"));

        ProductSku sku = new ProductSku();
        sku.setId(1L);
        sku.setPrice(new BigDecimal("199.99"));
        sku.setStockQuantity(200);

        ProductImage image = new ProductImage();
        image.setImageUrl("https://example.com/updated-image.jpg");
        image.setIsMain(true);

        productService.updateProduct(product, Arrays.asList(sku), Arrays.asList(image));

        Product updated = productService.getProduct(1L);
        assertEquals("更新后的商品名称", updated.getName());
    }
}
