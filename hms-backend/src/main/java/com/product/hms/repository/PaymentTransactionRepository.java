package com.product.hms.repository;

import com.product.hms.entity.PaymentTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransactionEntity, Long> {
    /**
     * Tìm kiếm PaymentTransactionEntity theo transactionReference.
     *
     * @param transactionReference transactionReference của giao dịch cần tìm kiếm
     * @return Optional chứa PaymentTransactionEntity nếu tìm thấy, hoặc Optional.empty() nếu không tìm thấy
     */
    Optional<PaymentTransactionEntity> findByTransactionReference(String transactionReference);
}
