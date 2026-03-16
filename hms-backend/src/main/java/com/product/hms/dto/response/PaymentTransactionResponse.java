package com.product.hms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class PaymentTransactionResponse {
    private Long id;
    private Long folioId;
    private String code;
    private String transactionReference;
    private String paymentMethod;
    private BigDecimal amount;
    private String type;
    private String status;
    private Timestamp createdAt;
}
