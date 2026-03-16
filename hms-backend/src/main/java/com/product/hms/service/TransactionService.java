package com.product.hms.service;

import com.product.hms.dto.response.PaymentTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    Page<PaymentTransactionResponse> getAllTransactions(String code, String paymentMethod, String type, String status, Long folioId, Pageable pageable);
    PaymentTransactionResponse getTransactionById(Long id);
}
