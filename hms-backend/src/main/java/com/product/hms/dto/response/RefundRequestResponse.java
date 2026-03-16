package com.product.hms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class RefundRequestResponse {
    private Long id;
    private BigDecimal amount;
    private String reason;
    private String rejectReason;
    private String status;
    private String requestedByName;
    private String approvedByName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private PaymentTransactionResponse paymentTransaction;
}
