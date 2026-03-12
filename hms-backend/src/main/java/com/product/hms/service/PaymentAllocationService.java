package com.product.hms.service;

import com.product.hms.entity.FolioItemEntity;
import com.product.hms.entity.PaymentTransactionEntity;

import java.util.List;

public interface PaymentAllocationService {
    /**
     * Tạo phân bổ thanh toán cho các mục hóa đơn (folio items) được cung cấp.
     *
     * @param paymentTransactionEntity
     * @param folioItemEntities        Danh sách các thực thể mục hóa đơn (folio items) để tạo phân bổ thanh toán.
     */
    void createPaymentAllocation(PaymentTransactionEntity paymentTransactionEntity, List<FolioItemEntity> folioItemEntities);
}
