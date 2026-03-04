package com.product.hms.repository;

import com.product.hms.entity.PaymentTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntity, Long> {

    Optional<PaymentTransactionEntity> findByTransactionReference(String transactionReference);
}

