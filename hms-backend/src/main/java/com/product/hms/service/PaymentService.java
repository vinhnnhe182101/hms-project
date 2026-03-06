package com.product.hms.service;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {

    String createVnPayPaymentUrl(Long folioId,
                                 BigDecimal amount,
                                 String clientIp,
                                 String returnUrl);

    void processVnPayIpn(Map<String, String> params);
}

