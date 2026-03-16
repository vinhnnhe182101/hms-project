package com.product.hms.repository;

import com.product.hms.entity.RefundRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequestEntity, Long>, JpaSpecificationExecutor<RefundRequestEntity> {
}
