package com.product.hms.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Response DTO for payment transaction
 *
 * @param paymentId          Payment transaction ID
 * @param paymentCode        Payment code
 * @param paymentMethod      Payment method
 * @param selectedItemsTotal Total amount of selected folio items
 * @param depositApplied     Deposit amount applied in this payment
 * @param cashCollected      Cash/card amount actually collected in this payment
 * @param status             Payment status
 * @param remainingBalance   Remaining folio balance after payment
 * @param paymentUrl         Redirect URL for VNPAY (null for non-VNPAY methods)
 * @param createdAt          Payment timestamp
 */
public record PaymentResponse(
        Long paymentId,
        String paymentCode,
        String paymentMethod,
        BigDecimal selectedItemsTotal,
        BigDecimal depositApplied,
        BigDecimal cashCollected,
        String status,
        BigDecimal remainingBalance,
        String paymentUrl,
        Timestamp createdAt
) {
}
