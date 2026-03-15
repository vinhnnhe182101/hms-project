package com.product.hms.repository;

import com.product.hms.entity.VnPayTransactionDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VnPayTransactionDetailRepository extends JpaRepository<VnPayTransactionDetailEntity, Long> {
    Optional<VnPayTransactionDetailEntity> findByPaymentTransactionEntityId(Long paymentTransactionId);
}
