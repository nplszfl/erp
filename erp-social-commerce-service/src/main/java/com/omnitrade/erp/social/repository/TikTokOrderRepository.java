package com.omnitrade.erp.social.repository;

import com.omnitrade.erp.social.model.TikTokOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * TikTok 订单仓储
 */
@Repository
public interface TikTokOrderRepository extends JpaRepository<TikTokOrder, Long> {
    
    /**
     * 根据订单ID查询
     */
    Optional<TikTokOrder> findByOrderId(String orderId);
    
    /**
     * 根据店铺ID查询订单
     */
    List<TikTokOrder> findByShopId(String shopId);
    
    /**
     * 根据订单状态查询
     */
    List<TikTokOrder> findByStatus(TikTokOrder.OrderStatus status);
    
    /**
     * 根据店铺ID和订单状态查询
     */
    List<TikTokOrder> findByShopIdAndStatus(String shopId, TikTokOrder.OrderStatus status);
    
    /**
     * 根据时间范围查询订单
     */
    @Query("SELECT o FROM TikTokOrder o WHERE o.createTime >= :startTime AND o.createTime <= :endTime")
    List<TikTokOrder> findByCreateTimeBetween(@Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据店铺ID和时间范围查询订单
     */
    @Query("SELECT o FROM TikTokOrder o WHERE o.shopId = :shopId AND o.createTime >= :startTime AND o.createTime <= :endTime")
    List<TikTokOrder> findByShopIdAndCreateTimeBetween(@Param("shopId") String shopId,
                                                        @Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据直播ID查询订单
     */
    List<TikTokOrder> findByLiveStreamId(Long liveStreamId);
    
    /**
     * 根据达人ID查询订单
     */
    List<TikTokOrder> findByInfluencerId(Long influencerId);
    
    /**
     * 查询待同步的订单
     */
    @Query("SELECT o FROM TikTokOrder o WHERE o.status IN ('PENDING', 'PAID', 'CONFIRMED') AND o.createTime >= :since")
    List<TikTokOrder> findUnsyncedOrders(@Param("since") LocalDateTime since);
    
    /**
     * 统计店铺订单数量
     */
    @Query("SELECT COUNT(o) FROM TikTokOrder o WHERE o.shopId = :shopId")
    long countByShopId(@Param("shopId") String shopId);
    
    /**
     * 统计店铺销售额
     */
    @Query("SELECT SUM(o.orderAmount) FROM TikTokOrder o WHERE o.shopId = :shopId AND o.status IN ('COMPLETED', 'DELIVERED')")
    java.math.BigDecimal sumSalesByShopId(@Param("shopId") String shopId);
    
    /**
     * 统计直播产生的订单
     */
    @Query("SELECT COUNT(o) FROM TikTokOrder o WHERE o.liveStreamId = :liveStreamId")
    long countByLiveStreamId(@Param("liveStreamId") Long liveStreamId);
    
    /**
     * 统计直播产生的销售额
     */
    @Query("SELECT SUM(o.orderAmount) FROM TikTokOrder o WHERE o.liveStreamId = :liveStreamId")
    java.math.BigDecimal sumSalesByLiveStreamId(@Param("liveStreamId") Long liveStreamId);
}