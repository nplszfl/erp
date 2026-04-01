package com.crossborder.tenant.repository;

import com.crossborder.tenant.entity.TenantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantUserRepository extends JpaRepository<TenantUser, String> {
    Optional<TenantUser> findByUsername(String username);
    Optional<TenantUser> findByEmail(String email);
    List<TenantUser> findByTenantId(String tenantId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}