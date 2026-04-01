package com.crossborder.common.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * 多租户Repository基接口
 * 所有需要多租户支持的Repository继承此接口
 */
@NoRepositoryBean
public interface MultiTenantRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    /**
     * 根据租户ID查询
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.tenantId = :tenantId")
    List<T> findByTenantId(String tenantId);

    /**
     * 根据租户ID和主键查询
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.tenantId = :tenantId")
    Optional<T> findByIdAndTenantId(ID id, String tenantId);

    /**
     * 检查租户下是否存在
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.id = :id AND e.tenantId = :tenantId")
    boolean existsByIdAndTenantId(ID id, String tenantId);
}