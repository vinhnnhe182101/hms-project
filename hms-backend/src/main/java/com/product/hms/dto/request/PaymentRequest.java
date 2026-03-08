package com.product.hms.dto.request;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for processing payment for a reservation room
 *
 * @param folioItemIds  List of folio item IDs selected by customer for this payment
 * @param paymentMethod Payment method (CASH, CARD, BANK_TRANSFER, QR, VNPAY)
 * @param depositAmount Deposit amount to deduct in this payment (optional)
 * @param clientIp      Client IP for VNPAY URL generation (required when paymentMethod = VNPAY)
 * @param returnUrl     Return URL for VNPAY redirect (required when paymentMethod = VNPAY)
 * @param note          Optional payment note
 */
public record PaymentRequest(
        List<Long> folioItemIds,
        String paymentMethod,
        BigDecimal depositAmount,
        String clientIp,
        String returnUrl,
        String note
) {
}
