package com.crossborder.tenant.repository;

import com.crossborder.tenant.entity.Tenant;
import com.crossborder.tenant.entity.TenantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    Optional<Tenant> findByDomain(String domain);
    List<Tenant> findByStatus(TenantStatus status);
    boolean existsByDomain(String domain);
}