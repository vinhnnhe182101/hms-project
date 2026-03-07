package com.product.hms.service;

import com.product.hms.entity.FolioEntity;

import java.math.BigDecimal;

public interface FolioItemService {
    void createFolioItemForDeposit(FolioEntity folio, BigDecimal depositAmount);

    /**
     * Create a refund folio item
     *
     * @param folio        folio entity
     * @param refundAmount refund amount
     */
    void createRefundItem(FolioEntity folio, BigDecimal refundAmount);

    /**
     * Create a cancellation fee folio item (adjustment, no refund)
     *
     * @param folio              folio entity
     * @param cancellationAmount cancellation fee amount
     */
    void createCancellationFeeItem(FolioEntity folio, BigDecimal cancellationAmount);
}
