// dto/response/customer/BookingDetailResponse.java
package com.product.hms.dto.response;

import com.product.hms.enums.ReservationStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class BookingDetailResponse {
    private Long id;
    private String code;
    private ReservationStatus status;
    private Timestamp checkIn;
    private Timestamp checkOut;
    private Integer nights;
    private String roomType;
    private String roomNumber;
    private Integer adults;
    private Integer children;
    private BigDecimal totalPrice;
    private BigDecimal paidAmount;
    private BigDecimal balance;
    private List<PaymentInfo> payments;
    private List<ServiceInfo> services;
    private List<MinibarInfo> minibar;
    private List<DamageInfo> damages;
    private Timestamp createdAt;

    @Data
    @Builder
    public static class PaymentInfo {
        private Timestamp date;
        private String method;
        private BigDecimal amount;
        private String status;
    }

    @Data
    @Builder
    public static class ServiceInfo {
        private String name;
        private Integer quantity;
        private BigDecimal price;
    }

    @Data
    @Builder
    public static class MinibarInfo {
        private String name;
        private Integer quantity;
        private BigDecimal price;
    }

    @Data
    @Builder
    public static class DamageInfo {
        private String description;
        private Integer quantity;
        private BigDecimal penalty;
    }
}