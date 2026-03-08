package com.product.hms.service;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.FolioItemEntity;
import com.product.hms.entity.ServiceBookingEntity;

import java.math.BigDecimal;
import java.util.Optional;

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

    void createEarlyCheckInFeeItem(FolioEntity folio, BigDecimal feeAmount);

    void createLateCheckOutFeeItem(FolioEntity folio, BigDecimal feeAmount);

    void createServiceChargeItem(FolioEntity folio, ServiceBookingEntity serviceBooking, BigDecimal chargeAmount);

    Optional<FolioItemEntity> findActiveByServiceBooking(ServiceBookingEntity serviceBooking);

    void updateServiceChargeItem(FolioItemEntity folioItem, Integer quantity, BigDecimal totalPrice);

    void voidServiceChargeItem(FolioItemEntity folioItem);

    /**
     * Calculate total charges from all active folio items.
     */
    BigDecimal calculateTotalCharges(FolioEntity folio);
}
