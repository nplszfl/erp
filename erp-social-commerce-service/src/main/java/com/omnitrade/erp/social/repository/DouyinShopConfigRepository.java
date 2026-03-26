package com.omnitrade.erp.social.repository;

import com.omnitrade.erp.social.model.DouyinShopConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DouyinShopConfigRepository extends JpaRepository<DouyinShopConfig, Long> {
    Optional<DouyinShopConfig> findByShopId(String shopId);
    Optional<DouyinShopConfig> findByShopName(String shopName);
}