package com.product.hms.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecentBookingDto {
    private Long reservationId;
    private String code;
    private String customerName;
    private LocalDateTime expectedCheckIn;
    private LocalDateTime expectedCheckOut;
    private String status;
}
