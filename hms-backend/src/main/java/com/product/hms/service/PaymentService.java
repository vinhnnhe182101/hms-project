package com.product.hms.service;

import com.product.hms.dto.request.PaymentRequest;
import com.product.hms.dto.response.PaymentResponse;
import com.product.hms.entity.FolioEntity;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {

    String createVnPayPaymentUrl(Long folioId,
                                 BigDecimal amount,
                                 String clientIp);

    String createVnPaymentUrlByPaymentTransactionId(long paymentTransactionId,
                                                    String clientIp);

    Map<String, String> processVnPayIpn(Map<String, String> params);

    /**
     * Process payment for a folio.
     * Creates payment transaction and allocates payment to unpaid folio items.
     * Updates folio balance and marks items as PAID when fully paid.
     *
     * @param folio   folio to pay
     * @param request payment details
     * @return PaymentResponse with payment info and remaining balance
     */
    PaymentResponse processPaymentForFolio(FolioEntity folio, PaymentRequest request);
}
