package com.product.hms.service;

import com.product.hms.entity.ReservationRoomEntity;

import java.math.BigDecimal;

public interface FolioService {
    void createFolioWithDepositItem(ReservationRoomEntity allocation, BigDecimal depositAmount);

    /**
     * Create a refund folio item (when canceling >24h before check-in)
     *
     * @param allocation   reservation room allocation
     * @param refundAmount amount to refund
     */
    void createRefundItem(ReservationRoomEntity allocation, BigDecimal refundAmount);

    /**
     * Create a cancellation fee folio item (when canceling <24h before check-in, no refund)
     *
     * @param allocation         reservation room allocation
     * @param cancellationAmount deposit amount that is not refunded
     */
    void createCancellationFeeItem(ReservationRoomEntity allocation, BigDecimal cancellationAmount);
}
