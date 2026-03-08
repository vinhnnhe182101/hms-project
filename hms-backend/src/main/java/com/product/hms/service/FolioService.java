package com.product.hms.service;

import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.ServiceBookingEntity;

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

    void applyEarlyCheckInFee(ReservationRoomEntity allocation, BigDecimal feeAmount);

    void applyLateCheckOutFee(ReservationRoomEntity allocation, BigDecimal feeAmount);

    /**
     * Sync folio charge for service booking creation/update.
     * If service item exists, update it; otherwise create a new one.
     */
    void updateServiceCharge(ServiceBookingEntity serviceBooking, BigDecimal chargeAmount);

    /**
     * Cancel service charge and adjust folio totals when service booking is canceled.
     */
    void cancelServiceCharge(ServiceBookingEntity serviceBooking);
}
