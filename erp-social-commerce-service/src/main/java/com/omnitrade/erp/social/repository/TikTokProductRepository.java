package com.omnitrade.erp.social.repository;

import com.omnitrade.erp.social.model.TikTokProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * TikTok 商品仓储
 */
@Repository
public interface TikTokProductRepository extends JpaRepository<TikTokProduct, Long> {
    
    /**
     * 根据商品ID查询
     */
    Optional<TikTokProduct> findByProductId(String productId);
    
    /**
     * 根据店铺ID查询商品
     */
    List<TikTokProduct> findByShopId(String shopId);
    
    /**
     * 根据商品状态查询
     */
    List<TikTokProduct> findByStatus(TikTokProduct.ProductStatus status);
    
    /**
     * 根据店铺ID和商品状态查询
     */
    List<TikTokProduct> findByShopIdAndStatus(String shopId, TikTokProduct.ProductStatus status);
    
    /**
     * 根据本地商品ID查询
     */
    Optional<TikTokProduct> findByLocalProductId(String localProductId);
    
    /**
     * 根据外部商品ID查询
     */
    Optional<TikTokProduct> findByExternalProductId(String externalProductId);
    
    /**
     * 查询直播商品
     */
    @Query("SELECT p FROM TikTokProduct p WHERE p.isLiveProduct = true AND p.status = 'ACTIVE'")
    List<TikTokProduct> findLiveProducts();
    
    /**
     * 根据店铺ID查询直播商品
     */
    @Query("SELECT p FROM TikTokProduct p WHERE p.shopId = :shopId AND p.isLiveProduct = true AND p.status = 'ACTIVE'")
    List<TikTokProduct> findLiveProductsByShopId(@Param("shopId") String shopId);
    
    /**
     * 根据商品名称模糊查询
     */
    @Query("SELECT p FROM TikTokProduct p WHERE p.productName LIKE %:keyword%")
    List<TikTokProduct> searchByName(@Param("keyword") String keyword);
    
    /**
     * 根据店铺ID和名称模糊查询
     */
    @Query("SELECT p FROM TikTokProduct p WHERE p.shopId = :shopId AND p.productName LIKE %:keyword%")
    List<TikTokProduct> searchByNameAndShopId(@Param("shopId") String shopId, @Param("keyword") String keyword);
    
    /**
     * 查询可挂载到直播的商品（库存充足且上架状态）
     */
    @Query("SELECT p FROM TikTokProduct p WHERE p.shopId = :shopId AND p.status = 'ACTIVE' AND p.stockQuantity > 0")
    List<TikTokProduct> findAvailableForLive(@Param("shopId") String shopId);
    
    /**
     * 统计店铺商品数量
     */
    @Query("SELECT COUNT(p) FROM TikTokProduct p WHERE p.shopId = :shopId")
    long countByShopId(@Param("shopId") String shopId);
    
    /**
     * 统计店铺活跃商品数量
     */
    @Query("SELECT COUNT(p) FROM TikTokProduct p WHERE p.shopId = :shopId AND p.status = 'ACTIVE'")
    long countActiveByShopId(@Param("shopId") String shopId);
    
    /**
     * 查询低库存商品
     */
    @Query("SELECT p FROM TikTokProduct p WHERE p.shopId = :shopId AND p.stockQuantity <= :threshold AND p.stockQuantity > 0")
    List<TikTokProduct> findLowStockProducts(@Param("shopId") String shopId, @Param("threshold") Integer threshold);
}