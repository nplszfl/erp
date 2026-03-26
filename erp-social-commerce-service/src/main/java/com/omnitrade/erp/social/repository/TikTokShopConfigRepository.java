package com.omnitrade.erp.social.repository;

import com.omnitrade.erp.social.model.TikTokShopConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TikTokShopConfigRepository extends JpaRepository<TikTokShopConfig, Long> {
    Optional<TikTokShopConfig> findByShopId(String shopId);
    Optional<TikTokShopConfig> findByShopName(String shopName);
}