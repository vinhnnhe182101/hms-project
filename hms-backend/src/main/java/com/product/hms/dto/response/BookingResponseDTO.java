package com.product.hms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long reservationId;
    private String reservationCode;
    private BigDecimal totalAmount;
    private BigDecimal depositAmount;
    private String paymentUrl;
}
