// dto/response/customer/BookingHistoryResponse.java
package com.product.hms.dto.response;

import com.product.hms.enums.ReservationStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
public class BookingHistoryResponse {
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
    private Timestamp createdAt;
    private Boolean hasReviewed;  // THÊM TRƯỜNG NÀY

    public String getStatusDisplay() {
        switch(status) {
            case PENDING_DEPOSIT: return "Pending";
            case CONFIRMED: return "Confirmed";
            case IN_HOUSE: return "In House";
            case CHECKED_OUT: return "Completed";
            case FINISHED: return "Finished";
            case CANCELLED: return "Cancelled";
            default: return status.name();
        }
    }

    public String getStatusColor() {
        switch(status) {
            case PENDING_DEPOSIT: return "yellow";
            case CONFIRMED: return "blue";
            case IN_HOUSE: return "teal";
            case CHECKED_OUT: return "green";
            case FINISHED: return "green";
            case CANCELLED: return "red";
            default: return "gray";
        }
    }
}