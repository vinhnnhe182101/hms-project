package com.product.hms.service.impl;

import com.product.hms.entity.FolioItemEntity;
import com.product.hms.entity.PaymentAllocationEntity;
import com.product.hms.entity.PaymentTransactionEntity;
import com.product.hms.repository.PaymentAllocationRepository;
import com.product.hms.service.PaymentAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentAllocationServiceImpl implements PaymentAllocationService {
    private final PaymentAllocationRepository paymentAllocationRepository;

    @Override
    @Transactional
    public void createPaymentAllocation(PaymentTransactionEntity paymentTransactionEntity, List<FolioItemEntity> folioItemEntities) {
        for (FolioItemEntity folioItemEntity : folioItemEntities) {
            PaymentAllocationEntity paymentAllocationEntity = new PaymentAllocationEntity();
            paymentAllocationEntity.setPaymentTransactionEntity(paymentTransactionEntity);
            paymentAllocationEntity.setFolioItemEntity(folioItemEntity);
            paymentAllocationEntity.setAmountApplied(folioItemEntity.getTotalPrice());
            paymentAllocationEntity.setIsActive(true);
            paymentAllocationRepository.save(paymentAllocationEntity);
        }
    }
}
