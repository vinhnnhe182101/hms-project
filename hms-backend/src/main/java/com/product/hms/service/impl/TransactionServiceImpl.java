package com.product.hms.service.impl;

import com.product.hms.dto.response.PaymentTransactionResponse;
import com.product.hms.entity.PaymentTransactionEntity;
import com.product.hms.repository.PaymentTransactionRepository;
import com.product.hms.repository.specification.PaymentTransactionSpecification;
import com.product.hms.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public Page<PaymentTransactionResponse> getAllTransactions(String code, String paymentMethod, String type, String status, Long folioId, Pageable pageable) {
        var spec = PaymentTransactionSpecification.byFilter(code, paymentMethod, type, status, folioId);
        Page<PaymentTransactionEntity> page = paymentTransactionRepository.findAll(spec, pageable);
        return page.map(this::toDto);
    }

    @Override
    public PaymentTransactionResponse getTransactionById(Long id) {
        PaymentTransactionEntity entity = paymentTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
        return toDto(entity);
    }

    private PaymentTransactionResponse toDto(PaymentTransactionEntity e) {
        Long folioId = e.getFolioEntity() != null ? e.getFolioEntity().getId() : null;
        return new PaymentTransactionResponse(e.getId(), folioId, e.getCode(), e.getTransactionReference(), e.getPaymentMethod(), e.getAmount(), e.getType(), e.getStatus(), e.getCreatedAt());
    }
}
